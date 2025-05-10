package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeyuchun.entity.Admin;
import top.yeyuchun.entity.User;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.EmailService;

@RestController
@RequestMapping("email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // 发送邮箱验证码
    @GetMapping("sendEmail")
    public Result sendEmail(String email) {
        emailService.sendEmail(email);
        return Result.success();
    }

    // 查找Admin通过邮箱
    @GetMapping("findAdminByEmail")
    public Result findAdminByEmail(String email) {
        Admin admin = emailService.findAdminByEmail(email);
        return Result.success(admin);
    }

    // 查找user通过邮箱
    @GetMapping("findUserByEmail")
    public Result findUserByEmail(String email) {
        User user = emailService.findUserByEmail(email);
        return Result.success(user);
    }
}
