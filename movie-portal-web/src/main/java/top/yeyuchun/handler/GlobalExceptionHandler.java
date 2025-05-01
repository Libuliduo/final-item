package top.yeyuchun.handler;

/*
    全局异常处理
    @RestControllerAdvice 是 Spring Framework 中的一个注解
    结合了 @ControllerAdvice 和 @ResponseBody 的功能
    专门用于处理 RESTful Web 服务的全局异常、数据绑定、模型属性等
    它可以用来对所有的 REST 控制器进行统一的异常处理、全局数据绑定、响应处理等
*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.exception.LoginException;
import top.yeyuchun.result.Result;
import org.springframework.mail.MailException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException ex) {
        log.warn("系统业务出现异常，异常信息为：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    // 处理登录异常
    @ExceptionHandler(LoginException.class)
    public Result handleLoginException(LoginException ex) {
        return Result.error(401, "登录失败，token无效");
    }


    // 处理邮件发送异常
    @ExceptionHandler(MailException.class)
    public Result handleMailException(MailException ex) {
        log.error("邮件发送失败，异常信息为：{}", ex.getMessage());
        return Result.error("邮件发送失败，请检查邮箱地址是否正确或稍后再试");
    }

    // 兜底异常：上传图片过大会调用这个异常
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        ex.printStackTrace();
        log.error("系统出现异常，导致原因为：{},异常信息为：{}",ex.getCause(),ex.getMessage());
        return Result.error("系统出现异常，请稍后再试");
    }
}
