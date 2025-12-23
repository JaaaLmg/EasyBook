package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.*;
import com.ja.easybookbackend.mapper.CustomerMapper;
import com.ja.easybookbackend.pojo.Customer;
import com.ja.easybookbackend.util.JwtUtil;
import com.ja.easybookbackend.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 认证服务类
 */
@Service
public class AuthService {

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 注册响应
     */
    public ApiResponse<RegisterResponse> register(RegisterRequest registerRequest) {
        // 检查用户名是否已经存在
        Customer existingCustomer = customerMapper.findByUsername(registerRequest.getUsername());
        if (existingCustomer != null) {
            return ApiResponse.error(400, "用户名已存在");
        }

        // 创建新用户
        Customer customer = new Customer();
        String customerId = "C" + UUID.randomUUID().toString().substring(0, 9).toUpperCase();

        customer.setCustomerId(customerId);
        customer.setUserName(registerRequest.getUsername());
        customer.setPasswordHash(PasswordEncoder.encode(registerRequest.getPassword()));
        customer.setEmail(registerRequest.getEmail());
        customer.setRealName(registerRequest.getRealName());
        customer.setPhone(registerRequest.getPhone());
        customer.setAccountBalance(0.0f);
        customer.setCreditLevel(1);
        customer.setTotalPurchase(0.0f);
        customer.setRegistrationDate(LocalDateTime.now());
        customer.setAccountStatus("active");

        customerMapper.insert(customer);

        RegisterResponse response = new RegisterResponse();
        response.setCustomerId(customerId);
        response.setUsername(registerRequest.getUsername());

        return ApiResponse.success("注册成功", response);
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        // 根据用户名查找用户
        Customer customer = customerMapper.findByUsername(loginRequest.getUsername());
        if (customer == null) {
            return ApiResponse.error(400, "用户名或密码错误");
        }

        // 验证密码
        if (!PasswordEncoder.matches(loginRequest.getPassword(), customer.getPasswordHash())) {
            return ApiResponse.error(400, "用户名或密码错误");
        }

        // 更新最后登录时间
        customer.setLastLogin(LocalDateTime.now());
        customerMapper.updateLastLogin(customer);

        // 生成JWT Token
        String token = JwtUtil.generateToken(customer.getCustomerId());

        // 构造用户信息
        UserProfileResponse userProfile = new UserProfileResponse();
        userProfile.setCustomerId(customer.getCustomerId());
        userProfile.setUsername(customer.getUserName());
        userProfile.setEmail(customer.getEmail());
        userProfile.setRealName(customer.getRealName());
        userProfile.setPhone(customer.getPhone());
        userProfile.setAddress(customer.getAddress());
        userProfile.setIsAdmin(false); // 简化处理，默认不是管理员
        userProfile.setCreditLevel(customer.getCreditLevel());
        userProfile.setAccountBalance(Double.valueOf(customer.getAccountBalance()));
        userProfile.setTotalPurchase(Double.valueOf(customer.getTotalPurchase()));

        // 构造登录响应
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(token);
        loginResponse.setUser(userProfile);

        return ApiResponse.success("登录成功", loginResponse);
    }

    /**
     * 获取用户信息
     *
     * @param customerId 用户ID
     * @return 用户信息
     */
    public ApiResponse<UserProfileResponse> getUserProfile(String customerId) {
        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        UserProfileResponse profile = new UserProfileResponse();
        profile.setCustomerId(customer.getCustomerId());
        profile.setUsername(customer.getUserName());
        profile.setEmail(customer.getEmail());
        profile.setRealName(customer.getRealName());
        profile.setPhone(customer.getPhone());
        profile.setAddress(customer.getAddress());
        profile.setIsAdmin(false);
        profile.setCreditLevel(customer.getCreditLevel());
        profile.setAccountBalance(Double.valueOf(customer.getAccountBalance()));
        profile.setTotalPurchase(Double.valueOf(customer.getTotalPurchase()));

        return ApiResponse.success(profile);
    }

    /**
     * 更新用户资料
     *
     * @param customerId 用户ID
     * @param request 更新请求
     * @return 更新结果
     */
    public ApiResponse<String> updateProfile(String customerId, UpdateProfileRequest request) {
        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        // 更新用户资料
        if (request.getRealName() != null) {
            customer.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }

        customerMapper.updateProfile(customer);

        return ApiResponse.success("资料更新成功", "");
    }

    /**
     * 修改密码
     *
     * @param customerId 用户ID
     * @param request 修改密码请求
     * @return 修改结果
     */
    public ApiResponse<String> changePassword(String customerId, ChangePasswordRequest request) {
        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        // 验证旧密码
        if (!PasswordEncoder.matches(request.getCurrentPassword(), customer.getPasswordHash())) {
            return ApiResponse.error(400, "当前密码错误");
        }

        // 更新密码
        customer.setPasswordHash(PasswordEncoder.encode(request.getNewPassword()));
        customerMapper.updatePassword(customer);

        return ApiResponse.success("密码修改成功", "");
    }
}
