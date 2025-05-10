package top.yeyuchun.controller;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeyuchun.exception.BusinessException;
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

    @PostMapping("register")
    public Result register(@RequestBody Map<String,String> paramMap) {
        String ok = userService.registerUser(paramMap);
        return Result.success(ok);
    }


//    @PostMapping("updateUserPwd")
//    public Result updateUserPwd(@RequestBody Map<String, String> paramMap) {
//        String email = paramMap.get("email");
//        String newPwd = paramMap.get("newPassword");
//
//        if (StrUtil.isBlank(email) || StrUtil.isBlank(newPwd)) {
//            throw new BusinessException("用户ID或新密码不能为空");
//        }
//
//        userService.updatePwdByEmail(email, newPwd);
//        return Result.success();
//    }

    // 重置密码
    @PostMapping("resetUserPwd")
    public Result updateUserPwd(@RequestBody Map<String, String> paramMap) {
        String ok = userService.resetUserPwd(paramMap);
        return Result.success(ok);
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
