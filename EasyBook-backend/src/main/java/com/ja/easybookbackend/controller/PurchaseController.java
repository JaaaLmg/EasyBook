package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.pojo.PurchaseOrder;
import com.ja.easybookbackend.service.PurchaseService;
import com.ja.easybookbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ApiResponse<PageResult<PurchaseOrder>> list(@RequestParam(value = "page", required = false) Integer page,
                                                       @RequestParam(value = "page_size", required = false) Integer pageSize,
                                                       @RequestParam(value = "status", required = false) String status,
                                                       @RequestParam(value = "supplier_id", required = false) String supplierId) {
        return purchaseService.list(page, pageSize, status, supplierId);
    }

    @GetMapping("/{purchaseId}")
    public ApiResponse<java.util.Map<String, Object>> detail(@PathVariable("purchaseId") String purchaseId) {
        return purchaseService.detail(purchaseId);
    }

    @PostMapping
    public ApiResponse<java.util.Map<String, Object>> create(@RequestHeader("Authorization") String authorization,
                                                             @RequestBody java.util.Map<String, Object> body) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return purchaseService.create(customerId, body);
    }

    @PutMapping("/{purchaseId}/approve")
    public ApiResponse<String> approve(@RequestHeader("Authorization") String authorization,
                                       @PathVariable("purchaseId") String purchaseId,
                                       @RequestBody java.util.Map<String, Object> body) {
        String token = authorization.replace("Bearer ", "");
        String approver = JwtUtil.validateToken(token);
        Boolean approved = (Boolean) body.get("approved");
        return purchaseService.approve(purchaseId, Boolean.TRUE.equals(approved), approver);
    }

    @PostMapping("/{purchaseId}/receive")
    public ApiResponse<String> receive(@PathVariable("purchaseId") String purchaseId,
                                       @RequestBody java.util.Map<String, Object> body) {
        java.util.List<java.util.Map<String, Object>> items = (java.util.List<java.util.Map<String, Object>>) body.get("items");
        String ad = (String) body.get("actual_delivery");
        LocalDate actualDelivery = null;
        if (ad != null && !ad.isBlank()) {
            actualDelivery = LocalDate.parse(ad);
        }
        return purchaseService.receive(purchaseId, items, actualDelivery);
    }
}
