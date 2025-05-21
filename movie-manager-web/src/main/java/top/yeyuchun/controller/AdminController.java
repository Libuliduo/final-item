package top.yeyuchun.controller;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.AdminService;

import java.util.Map;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 管理员注册
    @PostMapping("register")
    public Result register(@RequestBody Map<String, String> paramMap) {
        String ok = adminService.registerAdmin(paramMap);
        return Result.success(ok);
    }

    // 管理员登录
    @PostMapping("login")
    public Result login(@RequestBody Map<String, String> paramMap) {
        String token = adminService.login(paramMap);
        return Result.success(token);
    }

    // 修改密码
    @PostMapping("updateAdminPwd")
    public Result updateAdminPwd(@RequestBody Map<String, String> paramMap) {
        String newPwd = paramMap.get("newPassword");
        String idStr = paramMap.get("id");

        if (StrUtil.isBlank(idStr) || StrUtil.isBlank(newPwd)) {
            throw new BusinessException("管理员ID或新密码不能为空");
        }

        Integer id = Integer.valueOf(idStr);
        adminService.updatePasswordById(id, newPwd);
        return Result.success();
    }

    // 忘记密码
    @PostMapping("resetAdminPwd")
    public Result resetAdminPwd(@RequestBody Map<String, String> paramMap) {
        String ok = adminService.resetPassword(paramMap);
        return Result.success(ok);
    }

}
