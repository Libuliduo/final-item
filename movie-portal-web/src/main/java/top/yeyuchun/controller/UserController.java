package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeyuchun.entity.User;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result login(@RequestBody Map<String,String> paramMap) {
        String token = userService.login(paramMap);
        return Result.success(token);
    }

    @GetMapping("verify")
    public Result verify(@RequestHeader("Authorization") String token){
        userService.verify(token);
        return Result.success();
    }

}
