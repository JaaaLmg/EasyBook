package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.pojo.Publisher;
import com.ja.easybookbackend.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publishers")
@CrossOrigin(origins = "*")
public class PublisherController {
    @Autowired
    private PublisherService publisherService;

    @GetMapping
    public ApiResponse<java.util.List<Publisher>> list(@RequestParam(value = "keyword", required = false) String keyword) {
        return publisherService.list(keyword);
    }
}
