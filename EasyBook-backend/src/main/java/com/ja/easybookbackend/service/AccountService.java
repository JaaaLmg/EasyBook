package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.mapper.CreditRuleMapper;
import com.ja.easybookbackend.mapper.CustomerMapper;
import com.ja.easybookbackend.mapper.OrderMapper;
import com.ja.easybookbackend.pojo.CreditRule;
import com.ja.easybookbackend.pojo.Customer;
import com.ja.easybookbackend.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AccountService {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CreditRuleMapper creditRuleMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public ApiResponse<String> recharge(String customerId, double amount, String method) {
        // 使用存储过程 sp_recharge 进行充值
        try {
            // 准备参数Map
            java.util.Map<String, Object> params = new HashMap<>();
            params.put("customerId", customerId);
            params.put("amount", amount);
            params.put("method", method);

            // 调用存储过程
            customerMapper.callRecharge(params);

            // 从OUT参数获取新余额
            Object newBalance = params.get("newBalance");
            return ApiResponse.success("充值成功，当前余额：" + newBalance, "");
        } catch (Exception e) {
            // 捕获存储过程抛出的错误（金额非法、客户不存在等）
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("充值金额必须大于0")) {
                return ApiResponse.error(400, "充值金额必须大于0");
            } else if (errorMsg != null && errorMsg.contains("客户不存在")) {
                return ApiResponse.error(404, "用户不存在");
            }
            return ApiResponse.error(500, "充值失败：" + errorMsg);
        }
    }

    public ApiResponse<Map<String, Object>> getBalance(String customerId) {
        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        List<CreditRule> rules = creditRuleMapper.list();
        if (rules == null || rules.isEmpty()) {
            rules = Arrays.asList(
                    new CreditRule(1, "一级会员", 0.10, false, 0, 0.0, ""),
                    new CreditRule(2, "二级会员", 0.15, false, 0, 1000.0, ""),
                    new CreditRule(3, "三级会员", 0.15, true, 500, 2000.0, ""),
                    new CreditRule(4, "四级会员", 0.20, true, 1000, 5000.0, ""),
                    new CreditRule(5, "五级会员", 0.25, true, 99999999, 10000.0, "")
            );
        }
        CreditRule current = rules.stream().filter(r -> r.getLevel().equals(customer.getCreditLevel())).findFirst().orElse(rules.get(0));
        double balance = customer.getAccountBalance();
        double available = balance + (current.getOverdraftEnabled() ? current.getOverdraftLimit() : 0);
        Map<String, Object> data = new HashMap<>();
        data.put("account_balance", balance);
        data.put("credit_level", customer.getCreditLevel());
        data.put("overdraft_limit", current.getOverdraftLimit());
        data.put("available_balance", available);
        return ApiResponse.success(data);
    }

    public ApiResponse<PageResult<Map<String, Object>>> getTransactions(String customerId, Integer page, Integer pageSize, String type, String startDate, String endDate) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;

        List<Order> rawOrders = orderMapper.list(customerId, null, 1000, 0);
        List<Map<String, Object>> events = new ArrayList<>();

        for (Order o : rawOrders) {
            if (o.getPaymentTime() != null) {
                Map<String, Object> ev = new HashMap<>();
                ev.put("id", o.getOrderId() + "_pay");
                ev.put("type", "payment");
                ev.put("amount", o.getActualAmount());
                ev.put("currency", "CNY");
                ev.put("time", o.getPaymentTime());
                ev.put("order_id", o.getOrderId());
                ev.put("order_no", o.getOrderNo());
                ev.put("description", "订单支付确认");
                ev.put("balance_change", 0.0);
                events.add(ev);
            }
            if (o.getShippingTime() != null) {
                Map<String, Object> ev = new HashMap<>();
                ev.put("id", o.getOrderId() + "_ship");
                ev.put("type", "debit");
                ev.put("amount", o.getActualAmount());
                ev.put("currency", "CNY");
                ev.put("time", o.getShippingTime());
                ev.put("order_id", o.getOrderId());
                ev.put("order_no", o.getOrderNo());
                ev.put("description", "订单发货扣款");
                ev.put("balance_change", -Math.abs(o.getActualAmount() == null ? 0.0 : o.getActualAmount()));
                events.add(ev);
            }
        }

        events.sort((a, b) -> {
            LocalDateTime ta = (LocalDateTime) a.get("time");
            LocalDateTime tb = (LocalDateTime) b.get("time");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        if (type != null && !type.isEmpty()) {
            events.removeIf(e -> !type.equals(e.get("type")));
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm[:ss]]");
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) start = LocalDateTime.parse(startDate.replace(" ", "T"), fmt);
        } catch (Exception ignored) {}
        try {
            if (endDate != null && !endDate.isEmpty()) end = LocalDateTime.parse(endDate.replace(" ", "T"), fmt);
        } catch (Exception ignored) {}
        final LocalDateTime startFilter = start;
        final LocalDateTime endFilter = end;
        if (startFilter != null) {
            events.removeIf(e -> {
                LocalDateTime t = (LocalDateTime) e.get("time");
                return t == null || t.isBefore(startFilter);
            });
        }
        if (endFilter != null) {
            events.removeIf(e -> {
                LocalDateTime t = (LocalDateTime) e.get("time");
                return t == null || t.isAfter(endFilter);
            });
        }

        int total = events.size();
        int from = Math.min((p - 1) * ps, total);
        int to = Math.min(from + ps, total);
        List<Map<String, Object>> items = new ArrayList<>(events.subList(from, to));
        PageResult<Map<String, Object>> result = new PageResult<>(items, total, p, ps);
        return ApiResponse.success(result);
    }
}
