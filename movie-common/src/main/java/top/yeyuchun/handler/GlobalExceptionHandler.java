package top.yeyuchun.handler;

/*
    全局异常处理类
    @RestControllerAdvice 是一个组合注解，包含了：
     @ControllerAdvice：对所有 @Controller 和 @RestController 进行增强处理（例如异常处理）
     @ResponseBody：默认所有方法返回 JSON，而不是视图

    适用于 RESTful Web 服务，常用于：
     全局异常捕获
     全局数据绑定（如设置统一的时间格式）
     全局数据预处理
*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.exception.LoginException;
import top.yeyuchun.result.Result;
import org.springframework.mail.MailException;

// @Slf4j：自动为类生成日志记录器 logger
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
        // 返回 401 未授权错误（token 无效或过期）
        return Result.error(401, "登录失败，token无效");
    }


    // 处理邮件发送异常
    @ExceptionHandler(MailException.class)
    public Result handleMailException(MailException ex) {
        log.error("邮件发送失败，异常信息为：{}", ex.getMessage());
        return Result.error("邮件发送失败，请检查邮箱地址是否正确或稍后再试");
    }

    // 兜底异常处理：捕获其他所有未被上面处理的方法捕获的异常
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        ex.printStackTrace();
        log.error("系统出现异常，导致原因为：{},异常信息为：{}",ex.getCause(),ex.getMessage());
        return Result.error("系统出现异常，请稍后再试");
    }
}
