package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.mapper.CustomerMapper;
import com.ja.easybookbackend.mapper.OrderMapper;
import com.ja.easybookbackend.pojo.Customer;
import com.ja.easybookbackend.pojo.Order;
import com.ja.easybookbackend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/customers")
@CrossOrigin(origins = "*")
public class AdminCustomerController {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AccountService accountService;

    /**
     * 管理员查看所有客户列表（分页/搜索/筛选）
     * GET /api/admin/customers?page=1&pageSize=20&keyword=张三&creditLevel=3&accountStatus=active
     */
    @GetMapping
    public ApiResponse<PageResult<Customer>> listCustomers(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer creditLevel,
            @RequestParam(required = false) String accountStatus) {

        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (p - 1) * ps;

        List<Customer> items = customerMapper.listAllWithFilters(keyword, creditLevel, accountStatus, ps, offset);
        Integer total = customerMapper.countWithFilters(keyword, creditLevel, accountStatus);

        // 隐藏密码字段
        for (Customer c : items) {
            c.setPasswordHash(null);
        }

        PageResult<Customer> result = new PageResult<>(items, total, p, ps);
        return ApiResponse.success(result);
    }

    /**
     * 管理员查看客户详情（包括订单历史）
     * GET /api/admin/customers/{customerId}
     */
    @GetMapping("/{customerId}")
    public ApiResponse<Map<String, Object>> getCustomerDetail(@PathVariable String customerId) {
        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            return ApiResponse.error(404, "客户不存在");
        }

        // 隐藏密码
        customer.setPasswordHash(null);

        // 获取订单历史（最近20条）
        List<Order> recentOrders = orderMapper.list(customerId, null, 20, 0);

        Map<String, Object> data = new HashMap<>();
        data.put("customer", customer);
        data.put("recent_orders", recentOrders);
        data.put("order_count", orderMapper.count(customerId, null));

        return ApiResponse.success(data);
    }

    /**
     * 管理员为客户充值
     * POST /api/admin/customers/{customerId}/recharge
     * Body: { "amount": 1000.0, "payment_method": "admin_recharge" }
     */
    @PostMapping("/{customerId}/recharge")
    public ApiResponse<String> recharge(@PathVariable String customerId,
                                        @RequestBody Map<String, Object> body) {
        Number amountNum = (Number) body.get("amount");
        String method = (String) body.getOrDefault("payment_method", "admin_recharge");

        if (amountNum == null) {
            return ApiResponse.error(400, "充值金额不能为空");
        }

        double amount = amountNum.doubleValue();
        if (amount <= 0) {
            return ApiResponse.error(400, "充值金额必须大于0");
        }

        // 调用AccountService的recharge方法（复用逻辑）
        return accountService.recharge(customerId, amount, method);
    }

    /**
     * 管理员调整客户信用等级
     * PUT /api/admin/customers/{customerId}/credit-level
     * Body: { "credit_level": 3 }
     */
    @PutMapping("/{customerId}/credit-level")
    public ApiResponse<String> updateCreditLevel(@PathVariable String customerId,
                                                  @RequestBody Map<String, Object> body) {
        Number levelNum = (Number) body.get("credit_level");
        if (levelNum == null) {
            return ApiResponse.error(400, "信用等级不能为空");
        }

        int level = levelNum.intValue();
        if (level < 1 || level > 5) {
            return ApiResponse.error(400, "信用等级必须在1-5之间");
        }

        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            return ApiResponse.error(404, "客户不存在");
        }

        customer.setCreditLevel(level);
        customerMapper.updateCreditLevel(customer);

        return ApiResponse.success("信用等级已调整为" + level + "级", "");
    }

    /**
     * 管理员冻结/解冻客户账户
     * PUT /api/admin/customers/{customerId}/status
     * Body: { "account_status": "frozen" }  // active, frozen, closed
     */
    @PutMapping("/{customerId}/status")
    public ApiResponse<String> updateAccountStatus(@PathVariable String customerId,
                                                    @RequestBody Map<String, Object> body) {
        String status = (String) body.get("account_status");
        if (status == null || status.isEmpty()) {
            return ApiResponse.error(400, "账户状态不能为空");
        }

        if (!status.equals("active") && !status.equals("frozen") && !status.equals("closed")) {
            return ApiResponse.error(400, "账户状态必须是: active, frozen, closed");
        }

        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            return ApiResponse.error(404, "客户不存在");
        }

        customer.setAccountStatus(status);
        customerMapper.updateAccountStatus(customer);

        String statusText = status.equals("active") ? "激活" : (status.equals("frozen") ? "冻结" : "关闭");
        return ApiResponse.success("账户已" + statusText, "");
    }

    /**
     * 管理员删除客户账户（软删除）
     * DELETE /api/admin/customers/{customerId}
     */
    @DeleteMapping("/{customerId}")
    public ApiResponse<String> deleteCustomer(@PathVariable String customerId) {
        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            return ApiResponse.error(404, "客户不存在");
        }

        // 软删除：将账户状态设为 closed
        customerMapper.softDelete(customerId);

        return ApiResponse.success("客户账户已删除", "");
    }
}
