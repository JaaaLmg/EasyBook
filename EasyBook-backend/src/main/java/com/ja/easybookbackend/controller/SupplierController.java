package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.pojo.Supplier;
import com.ja.easybookbackend.pojo.SupplierBook;
import com.ja.easybookbackend.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public ApiResponse<java.util.List<Supplier>> list(@RequestParam(value = "keyword", required = false) String keyword) {
        return supplierService.list(keyword);
    }

    @PostMapping
    public ApiResponse<Supplier> create(@RequestBody Supplier supplier) {
        return supplierService.create(supplier);
    }

    @PutMapping("/{supplierId}")
    public ApiResponse<Supplier> update(@PathVariable("supplierId") String supplierId, @RequestBody Supplier supplier) {
        return supplierService.update(supplierId, supplier);
    }

    @DeleteMapping("/{supplierId}")
    public ApiResponse<String> delete(@PathVariable("supplierId") String supplierId) {
        return supplierService.delete(supplierId);
    }

    @GetMapping("/{supplierId}/books")
    public ApiResponse<java.util.List<SupplierBook>> listBooks(@PathVariable("supplierId") String supplierId) {
        return supplierService.listBooks(supplierId);
    }

    @PostMapping("/{supplierId}/books")
    public ApiResponse<SupplierBook> upsertBook(@PathVariable("supplierId") String supplierId, @RequestBody SupplierBook sb) {
        sb.setSupplierId(supplierId);
        return supplierService.upsertBook(sb);
    }
}
