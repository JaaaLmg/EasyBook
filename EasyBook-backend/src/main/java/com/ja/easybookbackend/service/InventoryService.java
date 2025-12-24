package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.mapper.InventoryMapper;
import com.ja.easybookbackend.mapper.BookMapper;
import com.ja.easybookbackend.pojo.Book;
import com.ja.easybookbackend.pojo.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class InventoryService {
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BookMapper bookMapper;

    public ApiResponse<PageResult<com.ja.easybookbackend.pojo.Inventory>> list(Integer page, Integer pageSize, String keyword, String status, String sort) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        List<Inventory> all = inventoryMapper.search(keyword);
        for (Inventory inv : all) {
            int q = inv.getQuantity() == null ? 0 : inv.getQuantity();
            int s = inv.getSafetyStock() == null ? 0 : inv.getSafetyStock();
            inv.setAvailableQuantity(q);
            String st = q == 0 ? "out_of_stock" : (q < s ? "low" : "normal");
            inv.setStatus(st);
        }
        if (status != null && !status.isEmpty()) {
            java.util.Iterator<Inventory> it = all.iterator();
            while (it.hasNext()) {
                if (!status.equals(it.next().getStatus())) {
                    it.remove();
                }
            }
        }
        if ("available_quantity_asc".equals(sort)) {
            all.sort(java.util.Comparator.comparing(i -> i.getAvailableQuantity() == null ? 0 : i.getAvailableQuantity()));
        } else if ("available_quantity_desc".equals(sort)) {
            all.sort(java.util.Comparator.comparing((Inventory i) -> i.getAvailableQuantity() == null ? 0 : i.getAvailableQuantity()).reversed());
        }
        int total = all.size();
        int from = Math.min((p - 1) * ps, total);
        int to = Math.min(from + ps, total);
        List<Inventory> items = new ArrayList<>(all.subList(from, to));
        PageResult<Inventory> result = new PageResult<>(items, total, p, ps);
        return ApiResponse.success(result);
    }

    public ApiResponse<java.util.Map<String, Object>> detail(String isbn) {
        Inventory inv = inventoryMapper.findByIsbn(isbn);
        Book book = bookMapper.findByIsbn(isbn);
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        if (inv != null) {
            int q = inv.getQuantity() == null ? 0 : inv.getQuantity();
            int s = inv.getSafetyStock() == null ? 0 : inv.getSafetyStock();
            inv.setAvailableQuantity(q);
            String st = q == 0 ? "out_of_stock" : (q < s ? "low" : "normal");
            inv.setStatus(st);
        }
        data.put("inventory", inv);
        data.put("book", book);
        data.put("history", java.util.Collections.emptyList());
        data.put("related_shortages", java.util.Collections.emptyList());
        return ApiResponse.success(data);
    }

    public ApiResponse<String> update(String isbn, String operation, Integer quantity, String reason, String locationCode) {
        Inventory inv = inventoryMapper.findByIsbn(isbn);
        if (inv == null) {
            return ApiResponse.error(404, "库存不存在");
        }
        int q = quantity == null ? 0 : quantity;
        switch (operation) {
            case "in":
                inv.setQuantity((inv.getQuantity() == null ? 0 : inv.getQuantity()) + q);
                inv.setLastRestock(LocalDateTime.now());
                break;
            case "out":
                int current = inv.getQuantity() == null ? 0 : inv.getQuantity();
                int reserved = inv.getReservedQuantity() == null ? 0 : inv.getReservedQuantity();
                if (q > current - reserved) {
                    return ApiResponse.error(400, "可用库存不足");
                }
                inv.setQuantity(current - q);
                break;
            case "check":
                inv.setQuantity(q);
                inv.setLastCheck(LocalDateTime.now());
                break;
            default:
                return ApiResponse.error(400, "不支持的操作");
        }
        if (locationCode != null) {
            inv.setLocationCode(locationCode);
        }
        inventoryMapper.update(inv);
        if ("in".equals(operation) || "check".equals(operation)) {
            orderService.reconcileForIsbn(isbn);
        }
        return ApiResponse.success("库存更新成功", "");
    }

    public ApiResponse<java.util.List<Inventory>> lowStock() {
        List<Inventory> items = inventoryMapper.findLowStock();
        for (Inventory inv : items) {
            int q = inv.getQuantity() == null ? 0 : inv.getQuantity();
            int s = inv.getSafetyStock() == null ? 0 : inv.getSafetyStock();
            inv.setAvailableQuantity(q);
            String st = q == 0 ? "out_of_stock" : (q < s ? "low" : "normal");
            inv.setStatus(st);
        }
        return ApiResponse.success(items);
    }

    /**
     * 软删除库存（将库存清零并标记图书为out_of_print）
     * 采用软删除策略，避免破坏历史订单的数据完整性
     */
    public ApiResponse<String> deleteInventory(String isbn) {
        Inventory inv = inventoryMapper.findByIsbn(isbn);
        if (inv == null) {
            return ApiResponse.error(404, "库存记录不存在");
        }

        Book book = bookMapper.findByIsbn(isbn);
        if (book == null) {
            return ApiResponse.error(404, "图书不存在");
        }

        // 检查是否有预留库存（表示有待发货订单）
        int reserved = inv.getReservedQuantity() == null ? 0 : inv.getReservedQuantity();
        if (reserved > 0) {
            return ApiResponse.error(400, "该图书有" + reserved + "本预留库存，无法删除（存在待发货订单）");
        }

        // 软删除：将库存清零，图书状态标记为out_of_print
        inv.setQuantity(0);
        inv.setReservedQuantity(0);
        inv.setLastCheck(LocalDateTime.now());
        inventoryMapper.update(inv);

        // 更新图书状态为out_of_print（绝版/下架）
        bookMapper.updateStatus(isbn, "out_of_print");

        return ApiResponse.success("库存已删除（软删除），图书已标记为out_of_print", "");
    }
}
