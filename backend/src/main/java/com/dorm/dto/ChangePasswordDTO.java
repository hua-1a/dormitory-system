package com.dorm.dto;

import lombok.Data;

/**
 * 修改密码请求
 */
@Data
public class ChangePasswordDTO {

    private String username;

    private String oldPassword;

    private String newPassword;
}
