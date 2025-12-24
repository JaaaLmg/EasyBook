package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.mapper.*;
import com.ja.easybookbackend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private OutOfStockRecordMapper outOfStockRecordMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 获取仪表盘统计数据
     * GET /api/admin/dashboard/stats
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. 总体统计
        List<Order> allOrders = orderMapper.listAll(null, 999999, 0);
        List<Customer> allCustomers = customerMapper.list();

        // 总订单数
        stats.put("total_orders", allOrders.size());

        // 总客户数
        stats.put("total_customers", allCustomers.size());

        // 总销售额（已完成订单）
        double totalSales = allOrders.stream()
            .filter(o -> "delivered".equals(o.getOrderStatus()))
            .mapToDouble(o -> o.getActualAmount() == null ? 0.0 : o.getActualAmount())
            .sum();
        stats.put("total_sales", String.format("%.2f", totalSales));

        // 2. 今日数据
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 今日订单
        long todayOrders = allOrders.stream()
            .filter(o -> o.getOrderTime() != null && o.getOrderTime().isAfter(todayStart))
            .count();
        stats.put("today_orders", todayOrders);

        // 今日销售额（已完成）
        double todaySales = allOrders.stream()
            .filter(o -> o.getDeliveryTime() != null && o.getDeliveryTime().isAfter(todayStart))
            .filter(o -> "delivered".equals(o.getOrderStatus()))
            .mapToDouble(o -> o.getActualAmount() == null ? 0.0 : o.getActualAmount())
            .sum();
        stats.put("today_sales", String.format("%.2f", todaySales));

        // 今日新客户
        long todayNewCustomers = allCustomers.stream()
            .filter(c -> c.getRegistrationDate() != null && c.getRegistrationDate().isAfter(todayStart))
            .count();
        stats.put("today_new_customers", todayNewCustomers);

        // 3. 订单状态统计
        Map<String, Long> orderStatusStats = new HashMap<>();
        orderStatusStats.put("pending", allOrders.stream().filter(o -> "pending".equals(o.getOrderStatus())).count());
        orderStatusStats.put("paid", allOrders.stream().filter(o -> "paid".equals(o.getOrderStatus())).count());
        orderStatusStats.put("processing", allOrders.stream().filter(o -> "processing".equals(o.getOrderStatus())).count());
        orderStatusStats.put("shipped", allOrders.stream().filter(o -> "shipped".equals(o.getOrderStatus())).count());
        orderStatusStats.put("delivered", allOrders.stream().filter(o -> "delivered".equals(o.getOrderStatus())).count());
        orderStatusStats.put("cancelled", allOrders.stream().filter(o -> "cancelled".equals(o.getOrderStatus())).count());
        stats.put("order_status_stats", orderStatusStats);

        // 4. 库存预警数量
        List<Inventory> allInventory = inventoryMapper.search(null);
        long lowStockCount = allInventory.stream()
            .filter(inv -> {
                int qty = inv.getQuantity() == null ? 0 : inv.getQuantity();
                int safety = inv.getSafetyStock() == null ? 0 : inv.getSafetyStock();
                return qty < safety;
            })
            .count();
        stats.put("low_stock_count", lowStockCount);

        // 5. 缺书记录待处理数量
        long pendingShortages = outOfStockRecordMapper.count(null, "pending", null);
        stats.put("pending_shortages", pendingShortages);

        // 6. 最近7天销售趋势
        List<Map<String, Object>> salesTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            double daySales = allOrders.stream()
                .filter(o -> o.getDeliveryTime() != null)
                .filter(o -> o.getDeliveryTime().isAfter(dayStart) && o.getDeliveryTime().isBefore(dayEnd))
                .filter(o -> "delivered".equals(o.getOrderStatus()))
                .mapToDouble(o -> o.getActualAmount() == null ? 0.0 : o.getActualAmount())
                .sum();

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("sales", String.format("%.2f", daySales));
            salesTrend.add(dayData);
        }
        stats.put("sales_trend", salesTrend);

        // 7. 信用等级分布
        Map<String, Long> creditLevelStats = new HashMap<>();
        for (int level = 1; level <= 5; level++) {
            final int currentLevel = level;
            long count = allCustomers.stream()
                .filter(c -> c.getCreditLevel() != null && c.getCreditLevel() == currentLevel)
                .count();
            creditLevelStats.put("level_" + level, count);
        }
        stats.put("credit_level_stats", creditLevelStats);

        return ApiResponse.success(stats);
    }

    /**
     * 获取最近订单
     * GET /api/admin/dashboard/recent-orders?limit=10
     */
    @GetMapping("/recent-orders")
    public ApiResponse<List<Order>> getRecentOrders(@RequestParam(defaultValue = "10") Integer limit) {
        List<Order> orders = orderMapper.listAll(null, limit, 0);
        return ApiResponse.success(orders);
    }

    /**
     * 获取库存预警列表
     * GET /api/admin/dashboard/low-stock?limit=10
     */
    @GetMapping("/low-stock")
    public ApiResponse<List<Map<String, Object>>> getLowStockItems(@RequestParam(defaultValue = "10") Integer limit) {
        List<Inventory> allInventory = inventoryMapper.search(null);

        List<Map<String, Object>> lowStockItems = new ArrayList<>();
        for (Inventory inv : allInventory) {
            int qty = inv.getQuantity() == null ? 0 : inv.getQuantity();
            int safety = inv.getSafetyStock() == null ? 0 : inv.getSafetyStock();

            if (qty < safety) {
                Map<String, Object> item = new HashMap<>();
                item.put("isbn", inv.getIsbn());
                item.put("quantity", qty);
                item.put("safety_stock", safety);
                item.put("shortage", safety - qty);
                lowStockItems.add(item);

                if (lowStockItems.size() >= limit) break;
            }
        }

        return ApiResponse.success(lowStockItems);
    }

    /**
     * 获取热销图书
     * GET /api/admin/dashboard/top-books?limit=5
     */
    @GetMapping("/top-books")
    public ApiResponse<List<Map<String, Object>>> getTopBooks(@RequestParam(defaultValue = "5") Integer limit) {
        // 统计所有订单详情中的图书销量
        List<OrderDetail> allDetails = new ArrayList<>();
        List<Order> allOrders = orderMapper.listAll(null, 9999, 0);

        for (Order order : allOrders) {
            if ("delivered".equals(order.getOrderStatus())) {
                List<OrderDetail> details = orderDetailMapper.listByOrder(order.getOrderId());
                allDetails.addAll(details);
            }
        }

        // 按ISBN分组统计
        Map<String, Integer> isbnSalesMap = new HashMap<>();
        for (OrderDetail detail : allDetails) {
            String isbn = detail.getIsbn();
            int quantity = detail.getQuantity() == null ? 0 : detail.getQuantity();
            isbnSalesMap.put(isbn, isbnSalesMap.getOrDefault(isbn, 0) + quantity);
        }

        // 排序并取前N个
        List<Map<String, Object>> topBooks = new ArrayList<>();
        isbnSalesMap.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(limit)
            .forEach(entry -> {
                Map<String, Object> book = new HashMap<>();
                book.put("isbn", entry.getKey());
                book.put("sales_count", entry.getValue());
                topBooks.add(book);
            });

        return ApiResponse.success(topBooks);
    }
}
