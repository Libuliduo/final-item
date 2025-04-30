package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeyuchun.entity.User;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.UserService;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result login(@RequestBody User loginUser) {
        try {
            boolean loginSuccess = userService.login(loginUser);
            if (loginSuccess) {
                return Result.success("登录成功"); // 返回登录成功的消息
            } else {
                return Result.error("邮箱或密码错误"); // 返回错误消息
            }
        } catch (Exception e) {
            return Result.error("服务器错误");
        }
    }
}
