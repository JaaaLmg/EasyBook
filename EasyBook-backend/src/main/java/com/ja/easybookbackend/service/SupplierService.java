package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.mapper.SupplierBookMapper;
import com.ja.easybookbackend.mapper.BookMapper;
import com.ja.easybookbackend.mapper.SupplierMapper;
import com.ja.easybookbackend.pojo.Supplier;
import com.ja.easybookbackend.pojo.SupplierBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {
    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private SupplierBookMapper supplierBookMapper;
    @Autowired
    private BookMapper bookMapper;

    public ApiResponse<List<Supplier>> list(String keyword) {
        List<Supplier> items = supplierMapper.list(keyword);
        return ApiResponse.success(items);
    }

    public ApiResponse<Supplier> create(Supplier supplier) {
        if (supplier.getSupplierId() == null || supplier.getSupplierId().isBlank()) {
            supplier.setSupplierId(genId("S", 10));
        }
        supplier.setCreateTime(LocalDateTime.now());
        supplier.setUpdateTime(LocalDateTime.now());
        if (supplier.getCooperationStatus() == null || supplier.getCooperationStatus().isBlank()) {
            supplier.setCooperationStatus("active");
        }
        supplierMapper.insert(supplier);
        return ApiResponse.success(supplier);
    }

    public ApiResponse<Supplier> update(String supplierId, Supplier supplier) {
        Supplier existing = supplierMapper.findById(supplierId);
        if (existing == null) {
            return ApiResponse.error(404, "供应商不存在");
        }
        supplier.setSupplierId(supplierId);
        supplier.setUpdateTime(LocalDateTime.now());
        supplierMapper.update(supplier);
        Supplier latest = supplierMapper.findById(supplierId);
        return ApiResponse.success(latest);
    }

    public ApiResponse<String> delete(String supplierId) {
        Supplier existing = supplierMapper.findById(supplierId);
        if (existing == null) {
            return ApiResponse.error(404, "供应商不存在");
        }
        // 先删除关联的供应商图书记录，避免外键约束冲突或孤儿记录
        supplierBookMapper.deleteBySupplier(supplierId);
        // 再删除供应商
        supplierMapper.delete(supplierId);
        return ApiResponse.success("删除成功", "");
    }

    public ApiResponse<List<SupplierBook>> listBooks(String supplierId) {
        List<SupplierBook> items = supplierBookMapper.listBySupplier(supplierId);
        return ApiResponse.success(items);
    }

    public ApiResponse<SupplierBook> upsertBook(SupplierBook sb) {
        if (sb.getIsbn() == null || sb.getIsbn().isBlank()) {
            return ApiResponse.error(400, "ISBN不能为空");
        }
        sb.setIsbn(sb.getIsbn().trim());
        if (sb.getIsbn().length() > 20) {
            return ApiResponse.error(400, "ISBN长度不能超过20");
        }
        Supplier supplier = supplierMapper.findById(sb.getSupplierId());
        if (supplier == null) {
            return ApiResponse.error(404, "供应商不存在");
        }
        com.ja.easybookbackend.pojo.Book book = bookMapper.findByIsbn(sb.getIsbn());
        if (book == null) {
            return ApiResponse.error(404, "图书不存在");
        }
        sb.setLastUpdate(LocalDateTime.now());
        SupplierBook existing = supplierBookMapper.find(sb.getSupplierId(), sb.getIsbn());
        if (existing == null) {
            if (sb.getIsAvailable() == null) sb.setIsAvailable(Boolean.TRUE);
            if (sb.getMinOrderQuantity() == null) sb.setMinOrderQuantity(1);
            if (sb.getDeliveryDays() == null) sb.setDeliveryDays(7);
            supplierBookMapper.insert(sb);
        } else {
            supplierBookMapper.update(sb);
        }
        SupplierBook latest = supplierBookMapper.find(sb.getSupplierId(), sb.getIsbn());
        return ApiResponse.success(latest);
    }

    private String genId(String prefix, int maxLen) {
        String base = UUID.randomUUID().toString().replace("-", "");
        int len = Math.max(0, maxLen - prefix.length());
        return prefix + base.substring(0, len);
    }
}
