package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.mapper.AuthorMapper;
import com.ja.easybookbackend.mapper.BookMapper;
import com.ja.easybookbackend.mapper.InventoryMapper;
import com.ja.easybookbackend.pojo.Book;
import com.ja.easybookbackend.pojo.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private AuthorMapper authorMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private CreditService creditService;

    private final Map<String, List<Item>> carts = new java.util.concurrent.ConcurrentHashMap<>();

    public ApiResponse<Map<String, Object>> getCart(String customerId) {
        List<Item> list = carts.getOrDefault(customerId, new ArrayList<>());
        Map<String, Object> data = buildSummary(customerId, list);
        return ApiResponse.success(data);
    }

    public ApiResponse<Map<String, Object>> addItem(String customerId, String isbn, Integer quantity) {
        List<Item> list = carts.computeIfAbsent(customerId, k -> new ArrayList<>());
        Item found = null;
        for (Item it : list) {
            if (it.isbn.equals(isbn)) { found = it; break; }
        }
        if (found == null) {
            found = new Item();
            found.itemId = UUID.randomUUID().toString().substring(0, 12);
            found.isbn = isbn;
            found.quantity = 0;
            list.add(found);
        }
        found.quantity += (quantity == null ? 1 : quantity);
        Map<String, Object> item = buildItem(customerId, found);
        return ApiResponse.success(item);
    }

    public ApiResponse<Map<String, Object>> updateItem(String customerId, String itemId, Integer quantity) {
        List<Item> list = carts.getOrDefault(customerId, new ArrayList<>());
        for (Item it : list) {
            if (Objects.equals(it.itemId, itemId)) {
                it.quantity = quantity == null ? it.quantity : quantity;
                Map<String, Object> item = buildItem(customerId, it);
                return ApiResponse.success(item);
            }
        }
        return ApiResponse.error(404, "购物车条目不存在");
    }

    public ApiResponse<String> removeItem(String customerId, String itemId) {
        List<Item> list = carts.getOrDefault(customerId, new ArrayList<>());
        list.removeIf(it -> Objects.equals(it.itemId, itemId));
        return ApiResponse.success("删除成功", "");
    }

    public ApiResponse<String> clear(String customerId) {
        carts.remove(customerId);
        return ApiResponse.success("清空成功", "");
    }

    public List<Item> getItems(String customerId) {
        return carts.getOrDefault(customerId, new ArrayList<>());
    }

    private Map<String, Object> buildSummary(String customerId, List<Item> list) {
        java.util.List<Map<String, Object>> items = new ArrayList<>();
        double totalAmount = 0.0;
        for (Item it : list) {
            Book book = bookMapper.findByIsbn(it.isbn);
            if (book == null) continue;
            Inventory inv = inventoryMapper.findByIsbn(it.isbn);
            int quantity = inv == null || inv.getQuantity() == null ? 0 : inv.getQuantity();
            int reserved = inv == null || inv.getReservedQuantity() == null ? 0 : inv.getReservedQuantity();
            int available = Math.max(0, quantity - reserved);
            double unitPrice = book.getPrice() == null ? 0.0 : book.getPrice();
            double subtotal = unitPrice * (it.quantity == null ? 0 : it.quantity);
            totalAmount += subtotal;
            String authors = String.join(
                    ", ",
                    authorMapper.findAuthorsByIsbn(it.isbn).stream().map(a -> a.getAuthorName()).toList()
            );
            Map<String, Object> item = new HashMap<>();
            item.put("item_id", it.itemId);
            item.put("isbn", it.isbn);
            item.put("title", book.getTitle());
            item.put("cover_image", book.getCoverImage());
            item.put("authors", authors);
            item.put("unit_price", unitPrice);
            item.put("quantity", it.quantity);
            item.put("discount_rate", 0.0);
            item.put("subtotal", subtotal);
            item.put("available_stock", available);
            items.add(item);
        }
        double discountRate = (Double) creditService.getCurrentCreditInfo(customerId).getData().get("discount_rate");
        double discountAmount = totalAmount * discountRate;
        double finalAmount = totalAmount - discountAmount;
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("total_items", items.stream().mapToInt(x -> ((Integer) x.get("quantity"))).sum());
        data.put("total_amount", totalAmount);
        data.put("discount_amount", discountAmount);
        data.put("final_amount", finalAmount);
        data.put("discount_rate", discountRate);
        return data;
    }

    private Map<String, Object> buildItem(String customerId, Item it) {
        Book book = bookMapper.findByIsbn(it.isbn);
        Inventory inv = inventoryMapper.findByIsbn(it.isbn);
        int quantity = inv == null || inv.getQuantity() == null ? 0 : inv.getQuantity();
        int reserved = inv == null || inv.getReservedQuantity() == null ? 0 : inv.getReservedQuantity();
        int available = Math.max(0, quantity - reserved);
        double unitPrice = book.getPrice() == null ? 0.0 : book.getPrice();
        String authors = String.join(
                ", ",
                authorMapper.findAuthorsByIsbn(it.isbn).stream().map(a -> a.getAuthorName()).toList()
        );
        Map<String, Object> item = new HashMap<>();
        item.put("item_id", it.itemId);
        item.put("isbn", it.isbn);
        item.put("title", book.getTitle());
        item.put("cover_image", book.getCoverImage());
        item.put("authors", authors);
        item.put("unit_price", unitPrice);
        item.put("quantity", it.quantity);
        item.put("discount_rate", 0.0);
        item.put("subtotal", unitPrice * it.quantity);
        item.put("available_stock", available);
        return item;
    }

    public static class Item {
        public String itemId;
        public String isbn;
        public Integer quantity;
    }
}
