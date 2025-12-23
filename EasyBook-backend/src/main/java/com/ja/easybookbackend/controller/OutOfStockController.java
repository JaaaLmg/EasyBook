package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.pojo.OutOfStockRecord;
import com.ja.easybookbackend.service.OutOfStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OutOfStockController {
    @Autowired
    private OutOfStockService outOfStockService;

    @GetMapping("/admin/out-of-stock")
    public ApiResponse<PageResult<OutOfStockRecord>> list(@RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "page_size", required = false) Integer pageSize,
                                                          @RequestParam(value = "source_type", required = false) String sourceType,
                                                          @RequestParam(value = "status", required = false) String status,
                                                          @RequestParam(value = "keyword", required = false) String keyword) {
        return outOfStockService.list(page, pageSize, sourceType, status, keyword);
    }

    @PostMapping("/out-of-stock")
    public ApiResponse<OutOfStockRecord> register(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                  @RequestBody java.util.Map<String, Object> body) {
        String isbn = (String) body.get("isbn");
        Number rq = (Number) body.get("required_quantity");
        Number pr = (Number) body.get("priority");
        String remark = (String) body.get("remark");
        String sourceType = (String) body.get("source_type");
        String supplierId = (String) body.get("supplier_id");
        String customerId = null;
        if (authorization != null && !authorization.isBlank()) {
            String token = authorization.replace("Bearer ", "");
            try {
                customerId = com.ja.easybookbackend.util.JwtUtil.validateToken(token);
            } catch (Exception ignored) {}
        }
        if (sourceType == null || sourceType.isBlank()) {
            sourceType = customerId == null ? "manual" : "customer";
        }
        Integer required = rq == null ? 1 : rq.intValue();
        Integer priority = pr == null ? 1 : pr.intValue();
        return outOfStockService.register(isbn, required, priority, remark, sourceType, customerId, supplierId);
    }

    @PutMapping("/admin/out-of-stock/{recordId}")
    public ApiResponse<String> update(@PathVariable("recordId") String recordId,
                                      @RequestBody java.util.Map<String, Object> body) {
        Number rq = (Number) body.get("required_quantity");
        Number pr = (Number) body.get("priority");
        String status = (String) body.get("status");
        Integer required = rq == null ? null : rq.intValue();
        Integer priority = pr == null ? null : pr.intValue();
        return outOfStockService.update(recordId, required, priority, status);
    }

    @PostMapping("/admin/out-of-stock/scan")
    public ApiResponse<String> scan(@RequestBody(required = false) java.util.Map<String, Object> body) {
        Number ms = body == null ? null : (Number) body.get("min_safety");
        Integer minSafety = ms == null ? null : ms.intValue();
        return outOfStockService.scanInventoryAndGenerate(minSafety);
    }

    @PostMapping("/admin/out-of-stock/{recordId}/notify")
    public ApiResponse<String> notify(@PathVariable("recordId") String recordId) {
        return outOfStockService.notify(recordId);
    }
}
