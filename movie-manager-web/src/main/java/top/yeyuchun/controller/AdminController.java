package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import top.yeyuchun.entity.Admin;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.AdminService;

import java.util.Map;

@RestController
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("sendEmail")
    public Result sendEmail(String email) {
        adminService.sendEmail(email);
        return Result.success();
    }

    @PostMapping("register")
    public Result register(@RequestBody Map<String, String> paramMap) {
        String ok = adminService.registerAdmin(paramMap);
        return Result.success(ok);
    }

    @PostMapping("login")
    public Result login(@RequestBody Map<String, String> paramMap) {
        Admin admin = adminService.login(paramMap);
        if (admin != null) {
            return Result.success("登录成功");
        } else {
            return Result.error("账号或密码错误");
        }
    }
}
