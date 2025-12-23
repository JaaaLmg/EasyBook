package com.ja.easybookbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户信息响应DTO
 */
@Data
public class UserProfileResponse {
    
    @JsonProperty("customer_id")
    private String customerId;
    
    private String username;
    
    private String email;
    
    @JsonProperty("real_name")
    private String realName;
    
    private String phone;
    
    private String address;
    
    @JsonProperty("is_admin")
    private Boolean isAdmin;
    
    @JsonProperty("credit_level")
    private Integer creditLevel;
    
    @JsonProperty("account_balance")
    private Double accountBalance;

    @JsonProperty("total_purchase")
    private Double totalPurchase;
}
