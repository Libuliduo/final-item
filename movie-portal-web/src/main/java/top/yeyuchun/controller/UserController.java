package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result login(@RequestBody Map<String, String> paramMap) {
        String token = userService.login(paramMap);
        return Result.success(token);
    }

    @GetMapping("verify")
    public Result verify(@RequestHeader("Authorization") String token) {
        userService.verify(token);
        return Result.success();
    }

    // 从jwt获取用户id
    @GetMapping("/jwtUserId")
    public Result getJwtUserId(@RequestHeader("Authorization") String token) {
        return Result.success(userService.getJwtUserId(token));
    }

    @PostMapping("add")
    public Result add(@RequestParam Integer userId, @RequestParam Integer movieId) {
        userService.addFavorite(userId, movieId);
        return Result.success("收藏成功");
    }

    @PostMapping("/remove")
    public Result remove(@RequestParam Integer userId, @RequestParam Integer movieId) {
        userService.removeFavorite(userId, movieId);
        return Result.success("取消收藏");
    }

    @GetMapping("/isFavorite")
    public Result isFavorite(@RequestParam Integer userId, @RequestParam Integer movieId) {
        boolean isFavorite = userService.isFavorite(userId, movieId);
        return Result.success(isFavorite);
    }
}
