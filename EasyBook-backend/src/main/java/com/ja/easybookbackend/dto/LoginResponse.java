package com.ja.easybookbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户登录响应DTO
 */
@Data
public class LoginResponse {
    @JsonProperty("access_token")
    private String accessToken;
    private UserProfileResponse user;
}
