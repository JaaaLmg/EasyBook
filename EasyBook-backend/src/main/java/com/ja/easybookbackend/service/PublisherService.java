package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.mapper.PublisherMapper;
import com.ja.easybookbackend.pojo.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {
    @Autowired
    private PublisherMapper publisherMapper;

    public ApiResponse<java.util.List<Publisher>> list(String keyword) {
        List<Publisher> items = publisherMapper.findAll(keyword);
        return ApiResponse.success(items);
    }
}
