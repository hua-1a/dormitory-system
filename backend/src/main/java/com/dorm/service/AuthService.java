package com.dorm.service;

import com.dorm.entity.SysUser;
import com.dorm.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * 登录认证
 */
@Service
public class AuthService {

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 登录校验：用户名 + MD5 密码
     *
     * @return 登录成功返回用户（已去除密码），失败返回 null
     */
    public SysUser login(String username, String password) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }
        String md5 = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!md5.equals(user.getPassword())) {
            return null;
        }
        user.setPassword(null);
        return user;
    }

    /**
     * 修改密码：校验原密码后更新为新密码（MD5 存储）
     *
     * @return null 表示成功，否则返回错误信息
     */
    public String changePassword(String username, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            return "新密码长度不能少于 6 位";
        }
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            return "用户不存在";
        }
        String oldMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes(StandardCharsets.UTF_8));
        if (!oldMd5.equalsIgnoreCase(user.getPassword())) {
            return "原密码错误";
        }
        String newMd5 = DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8));
        user.setPassword(newMd5);
        sysUserMapper.updateById(user);
        return null;
    }
}
