package top.yeyuchun.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import top.yeyuchun.entity.Admin;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.exception.LoginException;
import top.yeyuchun.mapper.AdminMapper;
import top.yeyuchun.service.AdminService;
import top.yeyuchun.template.JWTTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Map;
import java.util.Random;

@Service
public class AdminServiceImpl implements AdminService {

    @Value("${spring.mail.username}")
    private String server;

    @Resource
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JWTTemplate jwtTemplate;

    @Override
    public String generateCode() {

        int code = 100000 + new Random().nextInt(900000);
        return String.valueOf(code);
    }

    @Override
    public void sendEmail(String email) {
        // 邮件标题
        String subject = "您正在注册popcorn管理员";
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
            throw new BusinessException("邮件发送失败，请稍后再试");
        }
    }

    @Override
    public String registerAdmin(Map<String, String> paramMap) {
        // 1.获取参数：邮箱、手机号、密码、验证码
        String email = paramMap.get("email");
        String tel = paramMap.get("tel");
        String password = paramMap.get("password");
        String code = paramMap.get("code");

        // 2.用huTool工具做参数校验
        if (StrUtil.isBlank(email) || StrUtil.isBlank(tel)) {
            throw new BusinessException("手机号码或邮箱不能为空");
        } else if(StrUtil.isBlank(password) || StrUtil.isBlank(code)) {
            throw new BusinessException("密码或验证码不能为空");
        }

        // 3.从Redis取出验证码
        Object redisCode = redisTemplate.opsForValue().get("REGISTER_CODE:" + email);

        // 判断redis中是否有该验证码
        if (redisCode == null) {
            throw new BusinessException("请先发送验证码");
        }

        // 4.验证码校验————与redis中比较
        if (!StrUtil.equals(redisCode.toString(),code)) {
            throw new BusinessException("请输入正确的验证码");
        }

        // 5.查询数据库是否存在该用户，若不存在则存入数据库
        Admin admin = adminMapper.findByEmail(email);
        if (admin == null) {
            admin = new Admin();
            admin.setEmail(email);
            admin.setTel(tel);
            admin.setPassword(password);
            adminMapper.save(admin);
        } else {
            throw new BusinessException("注册失败——该邮箱已存在");
        }

        // 若没问题，返回ok
        return "ok";
    }

    @Override
    public String login(Map<String, String> paramMap) {
        // 1.获取前端输入的账号密码
        String account = paramMap.get("account");
        String password = paramMap.get("loginPassword");
        String type = paramMap.get("type");

        // 2.参数校验
        if (StrUtil.isBlank(account) || StrUtil.isBlank(password)) {
            throw new BusinessException("账号或密码不能为空");
        }

        // 3.处理登录业务
        Admin admin = null;
        if ("email".equalsIgnoreCase(type)) {
            admin = adminMapper.findByEmail(account);
        } else if ("tel".equalsIgnoreCase(type)) {
            admin = adminMapper.findByTel(account);
        } else {
            throw new BusinessException("不存在该用户");
        }

        if (admin == null || !StrUtil.equals(admin.getPassword(), password)) {
            throw new BusinessException("此用户无权访问系统");
        }

        // 避免将password打包进jwt中，可能泄露的
        admin.setPassword(null);

        // 存放管理员数据
        Map<String, Object> map = BeanUtil.beanToMap(admin);

        // 生成token
        String token = jwtTemplate.createJWT(map);
        return token;
    }


    @Override
    public void verify(String token) {
        try {
            jwtTemplate.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LoginException();
        }
    }

    @Override
    public Admin findByEmail(String email) {
        Admin admin = adminMapper.findByEmail(email);
        return admin;
    }

    @Override
    public Admin findByTel(String tel) {
        Admin admin = adminMapper.findByTel(tel);
        return admin;
    }

    @Override
    public Integer getAdminIdByToken(String token) {
        Claims claims = jwtTemplate.parseJWT(token);
        // 提取 id 字段，并转换为 Integer 类型
        return claims.get("id", Integer.class);
    }

    @Override
    public String getAdminNameByToken(String token) {
        Claims claims = jwtTemplate.parseJWT(token);
        // 提取 name 字段，并转换成String类型
        return claims.get("name", String.class);
    }

    @Override
    public void updatePasswordById(Integer id, String newPassword) {
        Admin admin = adminMapper.findById(id);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        adminMapper.updatePasswordById(id,newPassword);
    }


}
