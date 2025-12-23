package com.ja.easybookbackend.dto;

import lombok.Data;

/**
 * 修改密码请求DTO
 */
@Data
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
}