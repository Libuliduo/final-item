package top.yeyuchun.controller;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.entity.User;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    // 发送注册验证码
    @GetMapping("/email/registerCode")
    public Result registerCode(@RequestParam String email) {
        String msg = userService.sendRegisterCode(email);
        return Result.success(msg);
    }

    // 发送忘记密码的验证码
    @GetMapping("/email/resetCode")
    public Result resetEmail(@RequestParam String email) {
        String msg = userService.sendResetCode(email);
        return Result.success(msg);
    }

    @PostMapping("register")
    public Result register(@RequestBody Map<String,String> paramMap) {
        String msg = userService.registerUser(paramMap);
        return Result.success(msg);
    }

    @PostMapping("login")
    public Result login(@RequestBody Map<String, String> paramMap) {
        String token = userService.login(paramMap);
        return Result.success(token);
    }

    // 修改密码
    @PostMapping("updatePassword")
    public Result updatePassword(@RequestBody Map<String, String> paramMap) {
        String idStr = paramMap.get("id");
        String newPwd = paramMap.get("newPassword");

        if (StrUtil.isBlank(idStr) || StrUtil.isBlank(newPwd)) {
            throw new BusinessException("用户ID或新密码不能为空");
        }

        Integer id = Integer.valueOf(idStr);
        userService.updatePwdById(id, newPwd);
        return Result.success("修改密码成功");
    }

    // 忘记密码
    @PostMapping("resetPassword")
    public Result resetPassword(@RequestBody Map<String, String> paramMap) {
        String msg = userService.resetPassword(paramMap);
        return Result.success(msg);
    }


    @PostMapping("updateInfo")
    public Result resetInfo(@RequestBody Map<String, String> paramMap) {
        // 1. 获取参数 用户id，用户名userName,头像地址url
        String idStr = paramMap.get("id");
        String userName = paramMap.get("userName");
        String url = paramMap.get("url");

        // 2. 检测是否存在该用户
        if (idStr.isEmpty()) throw new BusinessException("未获取到用户");
        Integer id = Integer.valueOf(idStr);
        User user = userService.findById(id);

        // 3. 检测信息是否变更，修改变更的信息
        if (user.getUsername() == userName && user.getAvatar() == url) {
            throw new BusinessException("为检测到信息变更");
        }

        if (user.getUsername() != userName) {
            userService.resetUserName(id,userName);
        }

        if (user.getAvatar() != url) {
            userService.resetUserAvatar(id, url);
        }

        return Result.success("修改成功");
    }

    @GetMapping("findById")
    public Result findById(@RequestParam Integer id) {
        User user = userService.findById(id);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("没有找到该用户");
        }
    }

    // 从jwt获取用户id
    @GetMapping("/jwt/getIdByToken")
    public Result getIdByToken(@RequestHeader("Authorization") String token) {
        Integer id = userService.getIdByToken(token);
        return Result.success(id);
    }

    @PostMapping("/favorite/addFavorite")
    public Result add(@RequestParam Integer userId, @RequestParam Integer movieId) {
        userService.addFavorite(userId, movieId);
        return Result.success("收藏成功");
    }

    @PostMapping("/favorite/removeFavorite")
    public Result remove(@RequestParam Integer userId, @RequestParam Integer movieId) {
        userService.removeFavorite(userId, movieId);
        return Result.success("取消收藏");
    }

    @GetMapping("/favorite/isFavorite")
    public Result isFavorite(@RequestParam Integer userId, @RequestParam Integer movieId) {
        boolean isFavorite = userService.isFavorite(userId, movieId);
        return Result.success(isFavorite);
    }

    // 通过id查询喜欢的影视
    @GetMapping("/favorite/findFavorite")
    public Result findFavorite(@RequestParam Integer userId) {
        List<Movie> favoriteMovieList = userService.findFavorByUserId(userId);
        return favoriteMovieList.isEmpty() ?
                Result.error("还没有收藏的电影呢") :
                Result.success(favoriteMovieList);
    }
}
