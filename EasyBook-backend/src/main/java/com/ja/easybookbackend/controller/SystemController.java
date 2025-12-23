package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.pojo.CreditRule;
import com.ja.easybookbackend.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "*")
public class SystemController {
    @Autowired
    private CreditService creditService;

    @GetMapping("/credit-rules")
    public ApiResponse<java.util.List<CreditRule>> creditRules() {
        return creditService.getRules();
    }
}

