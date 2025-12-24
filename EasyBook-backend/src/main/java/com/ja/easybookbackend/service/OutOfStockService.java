package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.mapper.BookMapper;
import com.ja.easybookbackend.mapper.InventoryMapper;
import com.ja.easybookbackend.mapper.OutOfStockRecordMapper;
import com.ja.easybookbackend.mapper.PublisherMapper;
import com.ja.easybookbackend.pojo.Book;
import com.ja.easybookbackend.pojo.Inventory;
import com.ja.easybookbackend.pojo.OutOfStockRecord;
import com.ja.easybookbackend.pojo.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OutOfStockService {
    @Autowired
    private OutOfStockRecordMapper outOfStockRecordMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private PublisherMapper publisherMapper;
    @Autowired
    private com.ja.easybookbackend.mapper.CustomerMapper customerMapper;

    public ApiResponse<PageResult<OutOfStockRecord>> list(Integer page, Integer pageSize, String sourceType, String status, String keyword) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (p - 1) * ps;
        List<OutOfStockRecord> items = outOfStockRecordMapper.list(sourceType, status, keyword, ps, offset);
        Integer total = outOfStockRecordMapper.count(sourceType, status, keyword);
        return ApiResponse.success(new PageResult<>(items, total, p, ps));
    }

    public ApiResponse<OutOfStockRecord> register(String isbn, Integer requiredQuantity, Integer priority, String remark, String sourceType, String customerId, String supplierId) {
        Book book = bookMapper.findByIsbn(isbn);

        // 允许客户询价系统中不存在的新书
        // 只有在非客户询价且图书不存在时才报错
        if (book == null && !"customer".equals(sourceType)) {
            return ApiResponse.error(404, "图书不存在");
        }

        List<OutOfStockRecord> existing = outOfStockRecordMapper.findActiveByIsbn(isbn);
        if (existing != null && !existing.isEmpty()) {
            OutOfStockRecord r = existing.getFirst();
            r.setRequiredQuantity(requiredQuantity == null ? r.getRequiredQuantity() : requiredQuantity);
            r.setPriority(priority == null ? r.getPriority() : priority);
            r.setRemark(remark);
            outOfStockRecordMapper.update(r);
            return ApiResponse.success(r);
        }
        OutOfStockRecord r = new OutOfStockRecord();
        r.setRecordId(genId("OS", 20));
        r.setIsbn(isbn);

        // 如果图书存在，使用真实书名；否则直接使用 isbn 参数作为临时书名
        // （因为客户可能在 isbn 字段输入的是书名而不是真实 ISBN）
        r.setBookTitle(book == null ? isbn : book.getTitle());

        // 只有图书存在时才查询出版社
        if (book != null) {
            Publisher pub = publisherMapper.findById(book.getPublisherId());
            r.setPublisherName(pub == null ? null : pub.getPublisherName());
        } else {
            r.setPublisherName(null);
        }
        r.setRequiredQuantity(requiredQuantity == null ? 1 : requiredQuantity);
        if (customerId != null && !customerId.isBlank()) {
            com.ja.easybookbackend.pojo.Customer c = customerMapper.findById(customerId);
            r.setCustomerId(customerId);
            r.setCustomerEmail(c == null ? null : c.getEmail());
            r.setCustomerPhone(c == null ? null : c.getPhone());
        } else {
            r.setCustomerId(null);
            r.setCustomerEmail(null);
            r.setCustomerPhone(null);
        }
        r.setSourceType(sourceType == null || sourceType.isBlank() ? "manual" : sourceType);
        r.setPriority(priority == null ? 1 : priority);
        r.setStatus("pending");
        r.setNotifyStatus("customer".equals(r.getSourceType()) ? "pending" : "not_required");
        r.setNotifyTime(null);
        r.setCreatedTime(LocalDateTime.now());
        r.setResolveTime(null);
        String m = remark;
        if (supplierId != null && !supplierId.isBlank()) {
            String s = "supplier:" + supplierId;
            m = (m == null || m.isBlank()) ? s : (m + " | " + s);
        }
        r.setRemark(m);
        outOfStockRecordMapper.insert(r);
        return ApiResponse.success(r);
    }

    public void createFromOrder(String customerId, String customerEmail, String customerPhone, String isbn, Integer requiredQuantity) {
        Book book = bookMapper.findByIsbn(isbn);
        if (book == null) return;
        List<OutOfStockRecord> existing = outOfStockRecordMapper.findActiveByIsbn(isbn);
        if (existing != null && !existing.isEmpty()) return;
        OutOfStockRecord r = new OutOfStockRecord();
        r.setRecordId(genId("OS", 20));
        r.setIsbn(isbn);
        r.setBookTitle(book.getTitle());
        Publisher pub = publisherMapper.findById(book.getPublisherId());
        r.setPublisherName(pub == null ? null : pub.getPublisherName());
        r.setRequiredQuantity(requiredQuantity == null ? 1 : requiredQuantity);
        r.setCustomerId(customerId);
        r.setCustomerEmail(customerEmail);
        r.setCustomerPhone(customerPhone);
        r.setSourceType("customer");
        r.setPriority(3);
        r.setStatus("pending");
        r.setNotifyStatus("pending");
        r.setNotifyTime(null);
        r.setCreatedTime(LocalDateTime.now());
        r.setResolveTime(null);
        r.setRemark(null);
        outOfStockRecordMapper.insert(r);
    }

    public ApiResponse<String> scanInventoryAndGenerate(Integer minSafety) {
        List<Inventory> lows = inventoryMapper.findLowStock();
        for (Inventory inv : lows) {
            int qty = inv.getQuantity() == null ? 0 : inv.getQuantity();
            int safe = inv.getSafetyStock() == null ? 0 : inv.getSafetyStock();
            int minSafe = minSafety == null ? safe : Math.max(minSafety, safe);
            int need = Math.max(1, minSafe - qty);
            List<OutOfStockRecord> existing = outOfStockRecordMapper.findActiveByIsbn(inv.getIsbn());
            if (existing != null && !existing.isEmpty()) continue;
            Book book = bookMapper.findByIsbn(inv.getIsbn());
            if (book == null) continue;
            OutOfStockRecord r = new OutOfStockRecord();
            r.setRecordId(genId("OS", 20));
            r.setIsbn(inv.getIsbn());
            r.setBookTitle(book.getTitle());
            Publisher pub = publisherMapper.findById(book.getPublisherId());
            r.setPublisherName(pub == null ? null : pub.getPublisherName());
            r.setRequiredQuantity(need);
            r.setCustomerId(null);
            r.setCustomerEmail(null);
            r.setCustomerPhone(null);
            r.setSourceType("auto");
            r.setPriority(2);
            r.setStatus("pending");
            r.setNotifyStatus("not_required");
            r.setNotifyTime(null);
            r.setCreatedTime(LocalDateTime.now());
            r.setResolveTime(null);
            r.setRemark(null);
            outOfStockRecordMapper.insert(r);
        }
        List<OutOfStockRecord> all = outOfStockRecordMapper.list(null, null, null, Integer.MAX_VALUE, 0);
        for (OutOfStockRecord r : all) {
            if (!"pending".equals(r.getStatus()) && !"processing".equals(r.getStatus())) continue;
            Inventory inv = inventoryMapper.findByIsbn(r.getIsbn());
            if (inv == null) continue;
            int qty = inv.getQuantity() == null ? 0 : inv.getQuantity();
            int safe = inv.getSafetyStock() == null ? 0 : inv.getSafetyStock();
            if (qty >= safe) {
                r.setStatus("resolved");
                r.setResolveTime(LocalDateTime.now());
                outOfStockRecordMapper.update(r);
            }
        }
        return ApiResponse.success("OK", "");
    }

    public ApiResponse<String> update(String recordId, Integer requiredQuantity, Integer priority, String status) {
        List<OutOfStockRecord> records = outOfStockRecordMapper.list(null, null, null, Integer.MAX_VALUE, 0);
        OutOfStockRecord target = null;
        for (OutOfStockRecord r : records) {
            if (recordId.equals(r.getRecordId())) {
                target = r;
                break;
            }
        }
        if (target == null) return ApiResponse.error(404, "记录不存在");
        if (requiredQuantity != null) target.setRequiredQuantity(requiredQuantity);
        if (priority != null) target.setPriority(priority);
        if (status != null && !status.isBlank()) {
            target.setStatus(status);
            if ("resolved".equals(status)) {
                target.setResolveTime(LocalDateTime.now());
            }
        }
        outOfStockRecordMapper.update(target);
        return ApiResponse.success("OK", "");
    }

    public ApiResponse<String> notify(String recordId) {
        OutOfStockRecord target = outOfStockRecordMapper.findById(recordId);
        if (target == null) return ApiResponse.error(404, "记录不存在");
        target.setNotifyStatus("sent");
        target.setNotifyTime(LocalDateTime.now());
        outOfStockRecordMapper.update(target);
        return ApiResponse.success("OK", "");
    }

    private String genId(String prefix, int maxLen) {
        String base = UUID.randomUUID().toString().replace("-", "");
        int len = Math.max(0, maxLen - prefix.length());
        return prefix + base.substring(0, len);
    }
}
