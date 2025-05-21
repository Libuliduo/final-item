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

    // 重置密码
    @PostMapping("resetUserPwd")
    public Result updateUserPwd(@RequestBody Map<String, String> paramMap) {
        String ok = userService.resetUserPwd(paramMap);
        return Result.success(ok);
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

    @PostMapping("editInfo")
    public Result resetInfo(@RequestBody Map<String, String> paramMap) {
        // 1. 获取参数 用户id，用户名userName,头像地址url
        String idStr = paramMap.get("id");
        String userName = paramMap.get("userName");
        String url = paramMap.get("url");

        if (idStr.isEmpty()) {
            throw new BusinessException("未获取到用户");
        }

        Integer id = Integer.valueOf(idStr);

        // 更新用户名
        if (StrUtil.isBlank(userName) || StrUtil.isEmpty(userName)) {
            throw new BusinessException("用户名不得为空");
        }

        // 更新头像
        if (StrUtil.isBlank(url) || StrUtil.isEmpty(url)) {
            throw new BusinessException("未成功上传头像");
        }

        userService.resetUserName(id, userName);
        userService.resetUserAvatar(id, url);

        return Result.success();
    }

    // 修改密码
    @PostMapping("updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> paramMap) {
        String idStr = paramMap.get("id");
        String newPwd = paramMap.get("newPassword");

        if (StrUtil.isBlank(idStr) || StrUtil.isBlank(newPwd)) {
            throw new BusinessException("用户ID或新密码不能为空");
        }

        Integer id = Integer.valueOf(idStr);
        userService.updatePasswordById(id, newPwd);
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

    // 通过id查询喜欢的影视
    @GetMapping("findFavorite")
    public Result findFavorite(@RequestParam Integer userId) {
        List<Movie> favoriteMovieList = userService.findFavorByUserId(userId);
        return favoriteMovieList.isEmpty() ?
                Result.error("还没有收藏的电影呢") :
                Result.success(favoriteMovieList);
    }
}
