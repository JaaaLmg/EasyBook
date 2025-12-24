package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.pojo.Inventory;
import com.ja.easybookbackend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ApiResponse<PageResult<Inventory>> list(@RequestParam(value = "page", required = false) Integer page,
                                                   @RequestParam(value = "page_size", required = false) Integer pageSize,
                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "status", required = false) String status,
                                                   @RequestParam(value = "sort", required = false) String sort) {
        return inventoryService.list(page, pageSize, keyword, status, sort);
    }

    @GetMapping("/{isbn}")
    public ApiResponse<java.util.Map<String, Object>> detail(@PathVariable("isbn") String isbn) {
        return inventoryService.detail(isbn);
    }

    @PutMapping("/{isbn}")
    public ApiResponse<String> update(@PathVariable("isbn") String isbn,
                                      @RequestBody java.util.Map<String, Object> body) {
        String operation = (String) body.getOrDefault("operation", "");
        Integer quantity = (Integer) body.getOrDefault("quantity", 0);
        String reason = (String) body.getOrDefault("reason", "");
        String locationCode = (String) body.get("location_code");
        return inventoryService.update(isbn, operation, quantity, reason, locationCode);
    }

    @GetMapping("/low-stock")
    public ApiResponse<java.util.List<Inventory>> lowStock() {
        return inventoryService.lowStock();
    }

    /**
     * 删除库存（软删除）
     * DELETE /api/admin/inventory/{isbn}
     */
    @DeleteMapping("/{isbn}")
    public ApiResponse<String> delete(@PathVariable("isbn") String isbn) {
        return inventoryService.deleteInventory(isbn);
    }
}
