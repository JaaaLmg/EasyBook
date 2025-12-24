package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @JsonProperty("customer_id")
    private String customerId;
    
    @JsonProperty("user_name")
    private String userName;
    
    @JsonProperty("password_hash")
    private String passwordHash;
    
    @JsonProperty("real_name")
    private String realName;
    
    private String email;
    private String phone;
    private String address;
    
    @JsonProperty("account_balance")
    private Float accountBalance;
    
    @JsonProperty("credit_level")
    private Integer creditLevel;
    
    @JsonProperty("total_purchase")
    private Float totalPurchase;
    
    @JsonProperty("registration_date")
    private LocalDateTime registrationDate;
    
    @JsonProperty("last_login")
    private LocalDateTime lastLogin;
    
    @JsonProperty("account_status")
    private String accountStatus;

    @JsonProperty("is_admin")
    private Boolean isAdmin;
}
