package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.mapper.CreditRuleMapper;
import com.ja.easybookbackend.mapper.CustomerMapper;
import com.ja.easybookbackend.pojo.CreditRule;
import com.ja.easybookbackend.pojo.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreditService {
    @Autowired
    private CreditRuleMapper creditRuleMapper;
    @Autowired
    private CustomerMapper customerMapper;

    public ApiResponse<List<CreditRule>> getRules() {
        List<CreditRule> rules = creditRuleMapper.list();
        if (rules == null || rules.isEmpty()) {
            rules = java.util.Arrays.asList(
                    new CreditRule(1, "一级会员", 0.10, false, 0, 0.0, ""),
                    new CreditRule(2, "二级会员", 0.15, false, 0, 1000.0, ""),
                    new CreditRule(3, "三级会员", 0.15, true, 500, 2000.0, ""),
                    new CreditRule(4, "四级会员", 0.20, true, 1000, 5000.0, ""),
                    new CreditRule(5, "五级会员", 0.25, true, 99999999, 10000.0, "")
            );
        }
        return ApiResponse.success(rules);
    }

    public ApiResponse<Map<String, Object>> getCurrentCreditInfo(String customerId) {
        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        List<CreditRule> rules = creditRuleMapper.list();
        if (rules == null || rules.isEmpty()) {
            rules = java.util.Arrays.asList(
                    new CreditRule(1, "一级会员", 0.10, false, 0, 0.0, ""),
                    new CreditRule(2, "二级会员", 0.15, false, 0, 1000.0, ""),
                    new CreditRule(3, "三级会员", 0.15, true, 500, 2000.0, ""),
                    new CreditRule(4, "四级会员", 0.20, true, 1000, 5000.0, ""),
                    new CreditRule(5, "五级会员", 0.25, true, 99999999, 10000.0, "")
            );
        }
        CreditRule current = rules.stream().filter(r -> r.getLevel().equals(customer.getCreditLevel())).findFirst().orElse(rules.get(0));
        Map<String, Object> data = new HashMap<>();
        data.put("current_level", current.getLevel());
        data.put("level_name", current.getLevelName());
        data.put("discount_rate", current.getDiscountRate());
        data.put("overdraft_enabled", current.getOverdraftEnabled());
        data.put("overdraft_limit", current.getOverdraftLimit());
        data.put("total_purchase", customer.getTotalPurchase());
        if (current.getLevel() < 5) {
            int nextLevel = current.getLevel() + 1;
            CreditRule next = rules.stream().filter(r -> r.getLevel() == nextLevel).findFirst().orElse(null);
            if (next != null) {
                double required = next.getMinTotalPurchase() == null ? 0.0 : next.getMinTotalPurchase();
                double remaining = required - customer.getTotalPurchase();
                java.util.Map<String, Object> nextInfo = new HashMap<>();
                nextInfo.put("level", next.getLevel());
                nextInfo.put("required_purchase", required);
                nextInfo.put("remaining", Math.max(0.0, remaining));
                data.put("next_level", nextInfo);
            }
        }
        return ApiResponse.success(data);
    }
}
