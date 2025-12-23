package com.ja.easybookbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户注册请求DTO
 */
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    
    @JsonProperty("real_name")
    private String realName;
    
    private String phone;
    
    @JsonProperty("address")
    private String address;
}