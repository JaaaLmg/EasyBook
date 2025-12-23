package com.ja.easybookbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户注册响应DTO
 */
@Data
public class RegisterResponse {
    
    @JsonProperty("customer_id")
    private String customerId;
    
    private String username;
}