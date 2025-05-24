package top.yeyuchun.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import top.yeyuchun.entity.Admin;
import top.yeyuchun.entity.User;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.mapper.EmailMapper;
import top.yeyuchun.service.EmailService;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String server;

    @Resource
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EmailMapper emailMapper;

    // 生成验证码
    @Override
    public String generateCode() {
        int code = 100000 + new Random().nextInt(900000);
        return String.valueOf(code);
    }

    @Override
    public Admin findAdminByEmail(String email) {
        Admin admin = emailMapper.findAdminByEmail(email);
        return admin;
    }

    @Override
    public User findUserByEmail(String email) {
        User user = emailMapper.findUserByEmail(email);
        return user;
    }

    @Override
    public void sendRegisterCode(String email) {
        // 邮件标题
        String subject = "您正在注册popcorn账号";
        // 生成随机验证码
        String code = generateCode();
        System.out.println("正在生成验证码："+ code);
        // 邮件内容
        String text = "尊敬的用户：您好！您正在进行注册账号操作，验证码为：" + code + " 有效时间为5分钟。";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 设置发送邮件地址
        simpleMailMessage.setFrom(server);
        // 设置接受邮件地址
        simpleMailMessage.setTo(email);
        // 设置邮件主题
        simpleMailMessage.setSubject(subject);
        // 设置邮件内容
        simpleMailMessage.setText(text);

        // 发送邮件
        try {
            javaMailSender.send(simpleMailMessage); // 没抛异常就说明发送成功

            // 邮件发送成功，存入验证码
            redisTemplate.opsForValue().set("REGISTER_CODE:" + email, code, Duration.ofMinutes(5));
            // 输出一下存放进去的验证码
            Object storedObjectCode = redisTemplate.opsForValue().get("REGISTER_CODE:" + email);
            if (storedObjectCode != null) {
                String storedCode = (String) storedObjectCode; // 强制转换为 String 类型
                System.out.println("存入 Redis 的验证码是: " + storedCode);  // 打印输出存入的验证码
            } else {
                System.out.println("验证码未找到或已过期");
            }
        } catch (MailException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void sendResetCode(String email) {
        // 邮件标题
        String subject = "您正在找回账号密码";
        // 生成随机验证码
        String code = generateCode();
        System.out.println("正在生成验证码："+ code);
        // 邮件内容
        String text = "尊敬的用户：您好！您正在进行找回账号密码操作，验证码为：" + code + " 有效时间为5分钟。";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 设置发送邮件地址
        simpleMailMessage.setFrom(server);
        // 设置接受邮件地址
        simpleMailMessage.setTo(email);
        // 设置邮件主题
        simpleMailMessage.setSubject(subject);
        // 设置邮件内容
        simpleMailMessage.setText(text);

        // 发送邮件
        try {
            javaMailSender.send(simpleMailMessage); // 没抛异常就说明发送成功

            // 邮件发送成功，存入验证码
            redisTemplate.opsForValue().set("RESET_CODE:" + email, code, Duration.ofMinutes(5));
            // 输出一下存放进去的验证码
            Object storedObjectCode = redisTemplate.opsForValue().get("RESET_CODE:" + email);
            if (storedObjectCode != null) {
                String storedCode = (String) storedObjectCode; // 强制转换为 String 类型
                System.out.println("存入 Redis 的验证码是: " + storedCode);  // 打印输出存入的验证码
            } else {
                System.out.println("验证码未找到或已过期");
            }
        } catch (MailException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

}
