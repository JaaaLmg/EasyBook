package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.mapper.*;
import com.ja.easybookbackend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ApiResponse<java.util.Map<String, Object>> pay(String orderId, String paymentMethod) {
        Order order = orderMapper.findById(orderId);
        if (order == null) return ApiResponse.error(404, "订单不存在");
        List<OrderDetail> details = orderDetailMapper.listByOrder(orderId);
        boolean stockOk = true;
        for (OrderDetail d : details) {
            Inventory inv = inventoryMapper.findByIsbn(d.getIsbn());
            int qty = inv == null || inv.getQuantity() == null ? 0 : inv.getQuantity();
            if (qty < (d.getQuantity() == null ? 0 : d.getQuantity())) {
                stockOk = false;
                // 生成缺书记录
                Customer c = customerMapper.findById(order.getCustomerId());
                String email = c == null ? null : c.getEmail();
                String phone = c == null ? null : c.getPhone();
                int need = (d.getQuantity() == null ? 0 : d.getQuantity()) - qty;
                if (need > 0) {
                    outOfStockService.createFromOrder(order.getCustomerId(), email, phone, d.getIsbn(), need);
                }
            }
        }
        if (!stockOk) {
            if (!"processing".equals(order.getOrderStatus())) {
                order.setOrderStatus("processing");
                orderMapper.updateStatus(order);
            }
            return ApiResponse.error(400, "库存不足，暂不可支付");
        }
        Customer customer = customerMapper.findById(order.getCustomerId());
        double balance = customer.getAccountBalance() == null ? 0.0 : customer.getAccountBalance().doubleValue();
        boolean overdraftEnabled = (Boolean) creditService.getCurrentCreditInfo(order.getCustomerId()).getData().get("overdraft_enabled");
        int overdraftLimit = (Integer) creditService.getCurrentCreditInfo(order.getCustomerId()).getData().get("overdraft_limit");
        double available = balance + (overdraftEnabled ? overdraftLimit : 0);
        double need = order.getActualAmount() == null ? 0.0 : order.getActualAmount();
        if (need > available) {
            return ApiResponse.error(400, "余额不足，无法支付");
        }
        customer.setAccountBalance((float) (balance - need));
        customerMapper.updateBalance(customer);
        double currentPurchase = customer.getTotalPurchase() == null ? 0.0 : customer.getTotalPurchase().doubleValue();
        customer.setTotalPurchase((float) (currentPurchase + need));
        customerMapper.updateTotalPurchase(customer);
        order.setOrderStatus("paid");
        order.setPaymentMethod(paymentMethod);
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateStatus(order);
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("order_status", order.getOrderStatus());
        resp.put("payment_time", order.getPaymentTime());
        return ApiResponse.success(resp);
    }

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

    public ApiResponse<String> confirm(String orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) return ApiResponse.error(404, "订单不存在");
        order.setOrderStatus("delivered");
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateStatus(order);
        return ApiResponse.success("确认收货成功", "");
    }

    public ApiResponse<java.util.Map<String, Object>> ship(String orderId, String deliveryCompany, String trackingNo) {
        Order order = orderMapper.findById(orderId);
        if (order == null) return ApiResponse.error(404, "订单不存在");
        List<OrderDetail> details = orderDetailMapper.listByOrder(orderId);
        for (OrderDetail d : details) {
            Inventory inv = inventoryMapper.findByIsbn(d.getIsbn());
            if (inv != null) {
                int quantity = inv.getQuantity() == null ? 0 : inv.getQuantity();
                int reserved = inv.getReservedQuantity() == null ? 0 : inv.getReservedQuantity();
                if (quantity < (d.getQuantity() == null ? 0 : d.getQuantity())) {
                    Customer c = customerMapper.findById(order.getCustomerId());
                    String email = c == null ? null : c.getEmail();
                    String phone = c == null ? null : c.getPhone();
                    int need = (d.getQuantity() == null ? 0 : d.getQuantity()) - quantity;
                    if (need > 0) {
                        outOfStockService.createFromOrder(order.getCustomerId(), email, phone, d.getIsbn(), need);
                    }
                    return ApiResponse.error(400, "库存不足，无法发货");
                }
                inv.setQuantity(Math.max(0, quantity - d.getQuantity()));
                inv.setReservedQuantity(Math.max(0, reserved - d.getQuantity()));
                inv.setLastRestock(inv.getLastRestock());
                inv.setLastCheck(LocalDateTime.now());
                inventoryMapper.update(inv);
            }
        }
        order.setOrderStatus("shipped");
        order.setShippingTime(LocalDateTime.now());
        orderMapper.updateStatus(order);

        DeliveryOrder delivery = new DeliveryOrder();
        delivery.setDeliveryId("D" + UUID.randomUUID().toString().substring(0, 12));
        delivery.setOrderId(orderId);
        delivery.setDeliveryNo("DEL" + System.currentTimeMillis());
        delivery.setDeliveryCompany(deliveryCompany);
        delivery.setTrackingNo(trackingNo);
        delivery.setDeliveryAddress(order.getShippingAddress());
        delivery.setDeliveryStatus("shipped");
        delivery.setShippingFee(order.getShippingFee());
        delivery.setPrepareTime(LocalDateTime.now());
        delivery.setShipTime(LocalDateTime.now());
        deliveryOrderMapper.insert(delivery);

        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("delivery_id", delivery.getDeliveryId());
        resp.put("delivery_no", delivery.getDeliveryNo());
        return ApiResponse.success(resp);
    }

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
