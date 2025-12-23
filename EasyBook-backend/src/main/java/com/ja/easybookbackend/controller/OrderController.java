package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.pojo.Order;
import com.ja.easybookbackend.service.OrderService;
import com.ja.easybookbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public ApiResponse<PageResult<Order>> list(@RequestHeader("Authorization") String authorization,
                                               @RequestParam(value = "status", required = false) String status,
                                               @RequestParam(value = "page", required = false) Integer page,
                                               @RequestParam(value = "page_size", required = false) Integer pageSize,
                                               @RequestParam(value = "all", required = false) Boolean all) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        if (Boolean.TRUE.equals(all)) {
            return orderService.listAll(page, pageSize, status);
        }
        return orderService.list(customerId, page, pageSize, status);
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<java.util.Map<String, Object>> detail(@PathVariable("orderId") String orderId) {
        return orderService.detail(orderId);
    }

    @PostMapping("/orders")
    public ApiResponse<java.util.Map<String, Object>> create(@RequestHeader("Authorization") String authorization,
                                                             @RequestBody java.util.Map<String, Object> body) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        String shippingAddress = (String) body.get("shipping_address");
        String recipientName = (String) body.get("recipient_name");
        String recipientPhone = (String) body.get("recipient_phone");
        String paymentMethod = (String) body.get("payment_method");
        String notes = (String) body.get("notes");
        return orderService.create(customerId, shippingAddress, recipientName, recipientPhone, paymentMethod, notes);
    }

    @PostMapping("/orders/{orderId}/pay")
    public ApiResponse<java.util.Map<String, Object>> pay(@PathVariable("orderId") String orderId,
                                                          @RequestBody java.util.Map<String, Object> body) {
        String method = (String) body.get("payment_method");
        return orderService.pay(orderId, method);
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ApiResponse<String> cancel(@PathVariable("orderId") String orderId,
                                      @RequestBody(required = false) java.util.Map<String, Object> body) {
        return orderService.cancel(orderId);
    }

    @PostMapping("/orders/{orderId}/confirm")
    public ApiResponse<String> confirm(@PathVariable("orderId") String orderId) {
        return orderService.confirm(orderId);
    }

    @DeleteMapping("/orders/{orderId}")
    public ApiResponse<String> delete(@PathVariable("orderId") String orderId) {
        return orderService.delete(orderId);
    }

    @PostMapping("/admin/orders/{orderId}/ship")
    public ApiResponse<java.util.Map<String, Object>> ship(@PathVariable("orderId") String orderId,
                                                           @RequestBody java.util.Map<String, Object> body) {
        String company = (String) body.get("delivery_company");
        String trackingNo = (String) body.get("tracking_no");
        return orderService.ship(orderId, company, trackingNo);
    }

    @DeleteMapping("/admin/orders/{orderId}")
    public ApiResponse<String> adminDelete(@PathVariable("orderId") String orderId) {
        return orderService.delete(orderId);
    }
}
