package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        String res = adminService.login(paramMap);
        return Result.success(res);
    }

    @GetMapping("verify")
    public Result verify(@RequestHeader("Authorization") String token) {
        adminService.verify(token);
        return Result.success();
    }
}
