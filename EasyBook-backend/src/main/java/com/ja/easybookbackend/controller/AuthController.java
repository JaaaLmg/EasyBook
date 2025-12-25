package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.*;
import com.ja.easybookbackend.service.AuthService;
import com.ja.easybookbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // 允许跨域请求
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile(@RequestHeader("Authorization") String authorization) {
        // 从Authorization头部提取token
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return authService.getUserProfile(customerId);
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/profile")
    public ApiResponse<String> updateProfile(@RequestHeader("Authorization") String authorization,
                                             @RequestBody UpdateProfileRequest request) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return authService.updateProfile(customerId, request);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestHeader("Authorization") String authorization,
                                              @RequestBody ChangePasswordRequest request) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        return authService.changePassword(customerId, request);
    }

    /**
     * 删除账户（需要验证密码）
     */
    @DeleteMapping("/profile")
    public ApiResponse<String> deleteAccount(@RequestHeader("Authorization") String authorization,
                                             @RequestBody java.util.Map<String, Object> body) {
        String token = authorization.replace("Bearer ", "");
        String customerId = JwtUtil.validateToken(token);
        String password = (String) body.get("password");

        if (password == null || password.isEmpty()) {
            return ApiResponse.error(400, "请输入密码以确认删除");
        }

        return authService.deleteAccount(customerId, password);
    }
}