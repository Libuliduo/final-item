package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.TokenService;

@RestController
@RequestMapping("token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    // 验证token是否有效
    @GetMapping("verify")
    public Result verify(@RequestHeader("Authorization") String token) {
        tokenService.verify(token);
        return Result.success();
    }

}
