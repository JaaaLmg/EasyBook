package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.service.AccountService;
import com.ja.easybookbackend.service.CreditService;
import com.ja.easybookbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*")
public class AccountController {
    @Autowired
    private CreditService creditService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/credit-info")
    public ApiResponse<java.util.Map<String, Object>> creditInfo(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return creditService.getCurrentCreditInfo(customerId);
    }

    @GetMapping("/balance")
    public ApiResponse<java.util.Map<String, Object>> balance(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return accountService.getBalance(customerId);
    }

    @PostMapping("/recharge")
    public ApiResponse<String> recharge(@RequestHeader("Authorization") String authorization,
                                        @RequestBody java.util.Map<String, Object> body) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        Number amountNum = (Number) body.get("amount");
        String method = (String) body.get("payment_method");
        double amount = amountNum == null ? 0 : amountNum.doubleValue();
        return accountService.recharge(customerId, amount, method);
    }

    @GetMapping("/transactions")
    public ApiResponse<PageResult<java.util.Map<String, Object>>> transactions(@RequestHeader("Authorization") String authorization,
                                                                               @RequestParam(value = "page", required = false) Integer page,
                                                                               @RequestParam(value = "page_size", required = false) Integer pageSize,
                                                                               @RequestParam(value = "type", required = false) String type,
                                                                               @RequestParam(value = "start_date", required = false) String startDate,
                                                                               @RequestParam(value = "end_date", required = false) String endDate) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return accountService.getTransactions(customerId, page, pageSize, type, startDate, endDate);
    }
}
