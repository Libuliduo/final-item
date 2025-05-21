package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.TokenService;

@RestController
@RequestMapping("token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    // 通过token获取adminId
    @GetMapping("getAdminIdByToken")
    public Result getAdminIdByToken(@RequestHeader("Authorization") String token) {
        Integer adminId = tokenService.getAdminIdByToken(token);
        return Result.success(adminId);
    }

    // 通过token获取admin.name
    @GetMapping("getAdminNameByToken")
    public Result getAdminNameByToken(@RequestHeader("Authorization") String token) {
        String adminName = tokenService.getAdminNameByToken(token);
        return Result.success(adminName);
    }

    // 验证token是否有效
    @GetMapping("verify")
    public Result verify(@RequestHeader("Authorization") String token) {
        tokenService.verify(token);
        return Result.success();
    }

}
