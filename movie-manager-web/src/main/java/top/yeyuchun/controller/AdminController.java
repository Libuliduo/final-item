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

    // 浏览器验证是否登录
    @GetMapping("verify")
    public Result verify(@RequestHeader("Authorization") String token) {
        adminService.verify(token);
        return Result.success();
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


    // 通过token获取adminId
    @GetMapping("getAdminIdByToken")
    public Result getAdminIdByToken(@RequestHeader("Authorization") String token) {
        Integer adminId = adminService.getAdminIdByToken(token);
        return Result.success(adminId);
    }

    // 通过token获取admin.name
    @GetMapping("getAdminNameByToken")
    public Result getAdminNameByToken(@RequestHeader("Authorization") String token) {
        String adminName = adminService.getAdminNameByToken(token);
        return Result.success(adminName);
    }

}
