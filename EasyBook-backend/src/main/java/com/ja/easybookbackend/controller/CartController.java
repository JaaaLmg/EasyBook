package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.service.CartService;
import com.ja.easybookbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public ApiResponse<java.util.Map<String, Object>> get(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return cartService.getCart(customerId);
    }

    @PostMapping("/items")
    public ApiResponse<java.util.Map<String, Object>> add(@RequestHeader("Authorization") String authorization,
                                                          @RequestBody java.util.Map<String, Object> body) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        String isbn = (String) body.get("isbn");
        Integer quantity = (Integer) body.get("quantity");
        return cartService.addItem(customerId, isbn, quantity);
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<java.util.Map<String, Object>> update(@RequestHeader("Authorization") String authorization,
                                                             @PathVariable("itemId") String itemId,
                                                             @RequestBody java.util.Map<String, Object> body) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        Integer quantity = (Integer) body.get("quantity");
        return cartService.updateItem(customerId, itemId, quantity);
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<String> remove(@RequestHeader("Authorization") String authorization,
                                      @PathVariable("itemId") String itemId) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return cartService.removeItem(customerId, itemId);
    }

    @DeleteMapping
    public ApiResponse<String> clear(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return cartService.clear(customerId);
    }
}
