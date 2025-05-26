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

    // 发送注册验证码
    @GetMapping("/email/registerCode")
    public Result registerCode(@RequestParam String email) {
        String msg = adminService.sendRegisterCode(email);
        return Result.success(msg);
    }

    // 发送忘记密码的验证码
    @GetMapping("/email/resetCode")
    public Result resetEmail(@RequestParam String email) {
        String msg = adminService.sendResetCode(email);
        return Result.success(msg);
    }

    // 管理员登录
    @PostMapping("login")
    public Result login(@RequestBody Map<String, String> paramMap) {
        String token = adminService.login(paramMap);
        return Result.success(token);
    }

    // 管理员注册
    @PostMapping("register")
    public Result register(@RequestBody Map<String, String> paramMap) {
        String msg = adminService.registerAdmin(paramMap);
        return Result.success(msg);
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
    @PostMapping("resetPassword")
    public Result resetPassword(@RequestBody Map<String, String> paramMap) {
        String msg = adminService.resetPassword(paramMap);
        return Result.success(msg);
    }

}
