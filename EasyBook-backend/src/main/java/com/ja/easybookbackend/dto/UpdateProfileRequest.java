package com.ja.easybookbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 更新用户资料请求DTO
 */
@Data
public class UpdateProfileRequest {
    
    @JsonProperty("real_name")
    private String realName;
    
    private String phone;
    
    private String address;
}