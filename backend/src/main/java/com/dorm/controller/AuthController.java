package com.dorm.controller;

import com.dorm.common.Result;
import com.dorm.dto.ChangePasswordDTO;
import com.dorm.dto.LoginDTO;
import com.dorm.entity.SysUser;
import com.dorm.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 登录认证
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody LoginDTO dto) {
        SysUser user = authService.login(dto.getUsername(), dto.getPassword());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.ok(user);
    }

    @PostMapping("/changePassword")
    public Result<Void> changePassword(@RequestBody ChangePasswordDTO dto) {
        String err = authService.changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());
        if (err != null) {
            return Result.error(err);
        }
        return Result.ok();
    }
}
