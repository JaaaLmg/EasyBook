package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.mapper.CategoryMapper;
import com.ja.easybookbackend.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public ApiResponse<List<Category>> list() {
        return ApiResponse.success(categoryMapper.listAll());
    }
}

