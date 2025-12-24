package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.mapper.*;
import com.ja.easybookbackend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;
    @Autowired
    private CartService cartService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CreditService creditService;
    @Autowired
    private OutOfStockService outOfStockService;

    public ApiResponse<PageResult<Order>> list(String customerId, Integer page, Integer pageSize, String status) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (p - 1) * ps;
        List<Order> items = orderMapper.list(customerId, status, ps, offset);
        Integer total = orderMapper.count(customerId, status);
        return ApiResponse.success(new PageResult<>(items, total, p, ps));
    }

    public ApiResponse<PageResult<Order>> listAll(Integer page, Integer pageSize, String status) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (p - 1) * ps;
        List<Order> items = orderMapper.listAll(status, ps, offset);
        Integer total = orderMapper.countAll(status);
        return ApiResponse.success(new PageResult<>(items, total, p, ps));
    }

    public ApiResponse<java.util.Map<String, Object>> detail(String orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) return ApiResponse.error(404, "订单不存在");
        List<OrderDetail> details = orderDetailMapper.listByOrder(orderId);
        DeliveryOrder delivery = deliveryOrderMapper.findByOrder(orderId);
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("order", order);
        data.put("details", details);
        data.put("delivery", delivery);
        return ApiResponse.success(data);
    }

    @Transactional
    public ApiResponse<java.util.Map<String, Object>> create(String customerId, String shippingAddress, String recipientName, String recipientPhone, String paymentMethod, String notes) {
        List<CartService.Item> items = cartService.getItems(customerId);
        if (items == null || items.isEmpty()) {
            return ApiResponse.error(400, "购物车为空");
        }
        double totalAmount = 0.0;
        double discountRate = (Double) creditService.getCurrentCreditInfo(customerId).getData().get("discount_rate");
        boolean hasStockAll = true;
        for (CartService.Item it : items) {
            Book book = bookMapper.findByIsbn(it.isbn);
            totalAmount += (book.getPrice() == null ? 0.0 : book.getPrice()) * it.quantity;
            Inventory invCheck = inventoryMapper.findByIsbn(it.isbn);
            int qty = invCheck == null || invCheck.getQuantity() == null ? 0 : invCheck.getQuantity();
            int need = (it.quantity == null ? 0 : it.quantity) - qty;
            if (need > 0) {
                hasStockAll = false;
                Customer c = customerMapper.findById(customerId);
                String email = c == null ? null : c.getEmail();
                String phone = c == null ? null : c.getPhone();
                outOfStockService.createFromOrder(customerId, email, phone, it.isbn, need);
            }
        }
        double discountAmount = totalAmount * discountRate;
        double actualAmount = totalAmount - discountAmount;
        Order order = new Order();
        order.setOrderId("O" + UUID.randomUUID().toString().substring(0, 12));
        order.setOrderNo("NO" + System.currentTimeMillis());
        order.setCustomerId(customerId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setActualAmount(actualAmount);
        order.setShippingFee(0.0);
        order.setOrderStatus(hasStockAll ? "pending" : "processing");
        order.setShippingAddress(shippingAddress);
        order.setRecipientName(recipientName);
        order.setRecipientPhone(recipientPhone);
        order.setPaymentMethod(paymentMethod);
        order.setOrderTime(LocalDateTime.now());
        order.setNotes(notes);
        orderMapper.insert(order);

        for (CartService.Item it : items) {
            Book book = bookMapper.findByIsbn(it.isbn);
            OrderDetail d = new OrderDetail();
            d.setDetailId("OD" + UUID.randomUUID().toString().substring(0, 12));
            d.setOrderId(order.getOrderId());
            d.setIsbn(it.isbn);
            d.setQuantity(it.quantity);
            double unit = book.getPrice() == null ? 0.0 : book.getPrice();
            d.setUnitPrice(unit);
            d.setDiscountRate(discountRate);
            d.setSubtotal(unit * it.quantity * (1 - discountRate));
            orderDetailMapper.insert(d);
            Inventory inv = inventoryMapper.findByIsbn(it.isbn);
            if (inv != null) {
                inv.setReservedQuantity((inv.getReservedQuantity() == null ? 0 : inv.getReservedQuantity()) + it.quantity);
                inventoryMapper.update(inv);
            }
        }
        cartService.clear(customerId);
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("order_id", order.getOrderId());
        resp.put("order_no", order.getOrderNo());
        resp.put("actual_amount", order.getActualAmount());
        return ApiResponse.success(resp);
    }

    @Transactional
    public ApiResponse<java.util.Map<String, Object>> pay(String orderId, String paymentMethod) {
        // 修改：使用存储过程 sp_pay_order（只检查余额并更新状态，不扣款）
        try {
            // 调用存储过程进行支付确认
            java.util.Map<String, Object> result = orderMapper.callPayOrder(orderId, paymentMethod);

            // 存储过程返回的结果
            return ApiResponse.success(result);
        } catch (Exception e) {
            // 捕获存储过程抛出的错误（余额不足、订单状态错误等）
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("可用余额不足")) {
                return ApiResponse.error(400, "可用余额不足，无法支付");
            } else if (errorMsg != null && errorMsg.contains("订单不存在")) {
                return ApiResponse.error(404, "订单不存在");
            } else if (errorMsg != null && errorMsg.contains("订单状态不允许支付")) {
                return ApiResponse.error(400, "订单状态不允许支付");
            }
            return ApiResponse.error(500, "支付失败：" + errorMsg);
        }
    }

    @Transactional
    public ApiResponse<String> cancel(String orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) return ApiResponse.error(404, "订单不存在");
        if ("shipped".equals(order.getOrderStatus()) || "delivered".equals(order.getOrderStatus())) {
            return ApiResponse.error(400, "已发货订单不可取消");
        }
        if ("paid".equals(order.getOrderStatus())) {
            Customer customer = customerMapper.findById(order.getCustomerId());
            double balance = customer.getAccountBalance() == null ? 0.0 : customer.getAccountBalance().doubleValue();
            double refund = order.getActualAmount() == null ? 0.0 : order.getActualAmount();
            customer.setAccountBalance((float) (balance + refund));
            customerMapper.updateBalance(customer);
            double currentPurchase = customer.getTotalPurchase() == null ? 0.0 : customer.getTotalPurchase().doubleValue();
            customer.setTotalPurchase((float) Math.max(0.0, currentPurchase - refund));
            customerMapper.updateTotalPurchase(customer);
        }
        order.setOrderStatus("cancelled");
        orderMapper.updateStatus(order);
        List<OrderDetail> details = orderDetailMapper.listByOrder(orderId);
        for (OrderDetail d : details) {
            Inventory inv = inventoryMapper.findByIsbn(d.getIsbn());
            if (inv != null) {
                int reserved = inv.getReservedQuantity() == null ? 0 : inv.getReservedQuantity();
                inv.setReservedQuantity(Math.max(0, reserved - d.getQuantity()));
                inventoryMapper.update(inv);
            }
        }
        return ApiResponse.success("订单取消成功", "");
    }

    @Transactional
    public ApiResponse<String> confirm(String orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) return ApiResponse.error(404, "订单不存在");
        order.setOrderStatus("delivered");
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateStatus(order);
        return ApiResponse.success("确认收货成功", "");
    }

    @Transactional
    public ApiResponse<java.util.Map<String, Object>> ship(String orderId, String deliveryCompany, String trackingNo) {
        // 修改：使用存储过程 sp_process_delivery（发货时扣款、扣库存、释放预留、创建发货单）
        try {
            // 调用存储过程进行发货处理
            java.util.Map<String, Object> result = orderMapper.callProcessDelivery(orderId, deliveryCompany, trackingNo);

            // 存储过程返回的结果包含：delivery_id, delivery_no, order_status, shipping_time
            return ApiResponse.success(result);
        } catch (Exception e) {
            // 捕获存储过程抛出的错误（余额不足、库存不足、订单状态错误等）
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("订单不存在")) {
                return ApiResponse.error(404, "订单不存在");
            } else if (errorMsg != null && errorMsg.contains("订单未支付")) {
                return ApiResponse.error(400, "订单未支付，无法发货");
            } else if (errorMsg != null && errorMsg.contains("账户余额不足")) {
                return ApiResponse.error(400, "账户余额不足，无法发货");
            } else if (errorMsg != null && errorMsg.contains("超过透支额度")) {
                return ApiResponse.error(400, "超过透支额度，无法发货");
            }
            return ApiResponse.error(500, "发货失败：" + errorMsg);
        }
    }

    @Transactional
    public ApiResponse<String> delete(String orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) return ApiResponse.error(404, "订单不存在");
        if (!"delivered".equals(order.getOrderStatus())) {
            return ApiResponse.error(400, "仅已送达订单可删除");
        }
        deliveryOrderMapper.deleteByOrder(orderId);
        orderDetailMapper.deleteByOrder(orderId);
        orderMapper.delete(orderId);
        return ApiResponse.success("订单已删除", "");
    }

    @Transactional
    public void reconcileForIsbn(String isbn) {
        List<String> orderIds = orderDetailMapper.listOrderIdsByIsbn(isbn);
        if (orderIds == null || orderIds.isEmpty()) return;
        for (String oid : orderIds) {
            Order o = orderMapper.findById(oid);
            if (o == null) continue;
            if (!"processing".equals(o.getOrderStatus())) continue;
            List<OrderDetail> details = orderDetailMapper.listByOrder(oid);
            boolean ok = true;
            for (OrderDetail d : details) {
                Inventory inv = inventoryMapper.findByIsbn(d.getIsbn());
                int qty = inv == null || inv.getQuantity() == null ? 0 : inv.getQuantity();
                if (qty < (d.getQuantity() == null ? 0 : d.getQuantity())) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                o.setOrderStatus("pending");
                orderMapper.updateStatus(o);
            }
        }
    }
}
