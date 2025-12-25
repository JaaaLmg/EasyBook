package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.mapper.InventoryMapper;
import com.ja.easybookbackend.mapper.PurchaseDetailMapper;
import com.ja.easybookbackend.mapper.PurchaseOrderMapper;
import com.ja.easybookbackend.mapper.PurchaseShortageMapMapper;
import com.ja.easybookbackend.mapper.BookMapper;
import com.ja.easybookbackend.mapper.OutOfStockRecordMapper;
import com.ja.easybookbackend.pojo.Inventory;
import com.ja.easybookbackend.pojo.PurchaseDetail;
import com.ja.easybookbackend.pojo.PurchaseOrder;
import com.ja.easybookbackend.pojo.PurchaseShortageMap;
import com.ja.easybookbackend.pojo.Book;
import com.ja.easybookbackend.pojo.OutOfStockRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;
    @Autowired
    private PurchaseShortageMapMapper purchaseShortageMapMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private OutOfStockRecordMapper outOfStockRecordMapper;

    public ApiResponse<PageResult<PurchaseOrder>> list(Integer page, Integer pageSize, String status, String supplierId) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (p - 1) * ps;
        List<PurchaseOrder> items = purchaseOrderMapper.list(supplierId, status, ps, offset);
        Integer total = purchaseOrderMapper.count(supplierId, status);
        return ApiResponse.success(new PageResult<>(items, total, p, ps));
    }

    public ApiResponse<Map<String, Object>> detail(String purchaseId) {
        PurchaseOrder order = purchaseOrderMapper.findById(purchaseId);
        if (order == null) return ApiResponse.error(404, "采购单不存在");
        List<PurchaseDetail> details = purchaseDetailMapper.listByPurchase(purchaseId);
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("order", order);
        resp.put("details", details);
        return ApiResponse.success(resp);
    }

    public ApiResponse<Map<String, Object>> create(String createBy, Map<String, Object> body) {
        String supplierId = (String) body.get("supplier_id");
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        Number od = (Number) body.get("order_date");
        Number ed = (Number) body.get("expected_delivery");
        if (items == null || items.isEmpty()) return ApiResponse.error(400, "采购明细不能为空");
        if (supplierId == null || supplierId.isBlank()) return ApiResponse.error(400, "供应商不能为空");
        double total = 0.0;
        for (Map<String, Object> it : items) {
            String isbn = (String) it.get("isbn");
            if (isbn == null || isbn.isBlank()) {
                return ApiResponse.error(400, "ISBN不能为空");
            }
            isbn = isbn.trim();
            if (isbn.length() > 20) {
                return ApiResponse.error(400, "ISBN长度不能超过20");
            }
            Book bookCheck = bookMapper.findByIsbn(isbn);
            if (bookCheck == null) {
                return ApiResponse.error(404, "图书不存在");
            }
            Number qn = (Number) it.get("quantity");
            Number upn = (Number) it.get("unit_price");
            int quantity = qn == null ? 1 : qn.intValue();
            double unit = upn == null ? 0.0 : upn.doubleValue();
            total += unit * quantity;
        }
        PurchaseOrder order = new PurchaseOrder();
        order.setPurchaseId(genId("P", 20));
        order.setPurchaseNo("PO" + System.currentTimeMillis());
        order.setSupplierId(supplierId);
        order.setTotalAmount(total);
        order.setStatus("draft");
        order.setOrderDate(null);
        order.setExpectedDelivery(null);
        order.setActualDelivery(null);
        order.setCreateTime(LocalDateTime.now());
        order.setCreateBy(createBy);
        order.setApproveTime(null);
        order.setApproveBy(null);
        purchaseOrderMapper.insert(order);
        for (Map<String, Object> it : items) {
            String isbn = (String) it.get("isbn");
            Number qn = (Number) it.get("quantity");
            Number upn = (Number) it.get("unit_price");
            Number rid = (Number) it.get("record_quantity");
            String recordId = (String) it.get("record_id");
            int quantity = qn == null ? 1 : qn.intValue();
            double unit = upn == null ? 0.0 : upn.doubleValue();
            PurchaseDetail d = new PurchaseDetail();
            d.setDetailId(genId("PD", 20));
            d.setPurchaseId(order.getPurchaseId());
            d.setIsbn(isbn == null ? null : isbn.trim());
            d.setQuantity(quantity);
            d.setUnitPrice(unit);
            d.setReceivedQuantity(0);
            d.setStatus("pending");
            purchaseDetailMapper.insert(d);
            if (recordId != null && !recordId.isBlank()) {
                int mapped = rid == null ? quantity : rid.intValue();
                PurchaseShortageMap map = new PurchaseShortageMap();
                map.setPurchaseId(order.getPurchaseId());
                map.setRecordId(recordId);
                map.setMappedQuantity(mapped);
                purchaseShortageMapMapper.insert(map);
            }
        }
        order.setStatus("submitted");
        if (od != null) order.setOrderDate(LocalDate.now());
        if (ed != null) order.setExpectedDelivery(LocalDate.now());
        purchaseOrderMapper.update(order);
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("purchase_id", order.getPurchaseId());
        resp.put("purchase_no", order.getPurchaseNo());
        return ApiResponse.success(resp);
    }

    public ApiResponse<String> approve(String purchaseId, boolean approved, String approveBy) {
        PurchaseOrder order = purchaseOrderMapper.findById(purchaseId);
        if (order == null) return ApiResponse.error(404, "采购单不存在");
        if (!"submitted".equals(order.getStatus()) && !"draft".equals(order.getStatus())) {
            return ApiResponse.error(400, "当前状态不可审批");
        }
        order.setStatus(approved ? "approved" : "cancelled");
        order.setApproveTime(LocalDateTime.now());
        order.setApproveBy(approveBy);
        purchaseOrderMapper.update(order);
        return ApiResponse.success("OK", "");
    }

    public ApiResponse<String> receive(String purchaseId, List<Map<String, Object>> receivedItems, LocalDate actualDelivery) {
        PurchaseOrder order = purchaseOrderMapper.findById(purchaseId);
        if (order == null) return ApiResponse.error(404, "采购单不存在");
        List<PurchaseDetail> details = purchaseDetailMapper.listByPurchase(purchaseId);
        java.util.Map<String, PurchaseDetail> byIsbn = new java.util.HashMap<>();
        for (PurchaseDetail d : details) byIsbn.put(d.getIsbn(), d);
        for (Map<String, Object> it : receivedItems) {
            String isbn = (String) it.get("isbn");
            Number qn = (Number) it.get("received_quantity");
            int qty = qn == null ? 0 : qn.intValue();
            PurchaseDetail d = byIsbn.get(isbn);
            if (d == null) continue;
            int currentReceived = d.getReceivedQuantity() == null ? 0 : d.getReceivedQuantity();
            int totalToReceive = Math.max(0, Math.min(d.getQuantity(), currentReceived + qty));
            d.setReceivedQuantity(totalToReceive);
            d.setStatus(totalToReceive >= d.getQuantity() ? "complete" : "partial");
            purchaseDetailMapper.update(d);
            // 注意：库存增加由数据库触发器 tr_purchase_received 负责（基于 received_quantity 的增量）
            // 这里不要再手动更新 inventory.quantity，否则会导致入库数量翻倍。
        }
        boolean allComplete = true;
        details = purchaseDetailMapper.listByPurchase(purchaseId);
        for (PurchaseDetail d : details) {
            if (!"complete".equals(d.getStatus())) {
                allComplete = false;
                break;
            }
        }
        order.setStatus("received");
        order.setActualDelivery(actualDelivery == null ? LocalDate.now() : actualDelivery);
        purchaseOrderMapper.update(order);
        List<PurchaseShortageMap> maps = purchaseShortageMapMapper.listByPurchase(purchaseId);
        for (PurchaseShortageMap map : maps) {
            List<OutOfStockRecord> rs = outOfStockRecordMapper.list(null, null, null, Integer.MAX_VALUE, 0);
            OutOfStockRecord target = null;
            for (OutOfStockRecord r : rs) {
                if (map.getRecordId().equals(r.getRecordId())) {
                    target = r;
                    break;
                }
            }
            if (target != null) {
                int need = target.getRequiredQuantity() == null ? 0 : target.getRequiredQuantity();
                int mapped = map.getMappedQuantity() == null ? 0 : map.getMappedQuantity();
                int remain = Math.max(0, need - mapped);
                if (remain <= 0) {
                    target.setStatus("resolved");
                    target.setResolveTime(LocalDateTime.now());
                } else {
                    target.setRequiredQuantity(remain);
                }
                outOfStockRecordMapper.update(target);
                purchaseShortageMapMapper.delete(map.getPurchaseId(), map.getRecordId());
            }
        }
        return ApiResponse.success("OK", "");
    }

    private String genId(String prefix, int maxLen) {
        String base = UUID.randomUUID().toString().replace("-", "");
        int len = Math.max(0, maxLen - prefix.length());
        return prefix + base.substring(0, len);
    }
}
