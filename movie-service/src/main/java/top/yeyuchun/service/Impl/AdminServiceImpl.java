package top.yeyuchun.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yeyuchun.entity.Admin;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.mapper.AdminMapper;
import top.yeyuchun.service.AdminService;
import top.yeyuchun.service.EmailService;
import top.yeyuchun.template.JWTTemplate;

import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JWTTemplate jwtTemplate;

    @Autowired
    private EmailService emailService;

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
        } else if (StrUtil.isBlank(password) || StrUtil.isBlank(code)) {
            throw new BusinessException("密码或验证码不能为空");
        }

        // 3.从Redis取出验证码
        Object redisCode = redisTemplate.opsForValue().get("REGISTER_CODE:" + email);

        // 判断redis中是否有该验证码
        if (redisCode == null) {
            throw new BusinessException("请先发送验证码");
        }

        // 4.验证码校验————与redis中比较
        if (!StrUtil.equals(redisCode.toString(), code)) {
            throw new BusinessException("请输入正确的验证码");
        }

        // 5.查询数据库是否存在该用户，若不存在则存入数据库
        Admin admin = emailService.findAdminByEmail(email);
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
    public String resetPassword(Map<String, String> paramMap) {
        // 1. 获取参数：邮箱、新密码、验证码
        String email = paramMap.get("email");
        String newPassword = paramMap.get("newPassword");
        String code = paramMap.get("code");

        // 2. 参数校验
        if (StrUtil.isBlank(email) || StrUtil.isBlank(newPassword)) {
            throw new BusinessException("邮箱和新密码不能为空");
        } else if (StrUtil.isBlank(code)) {
            throw new BusinessException("验证码不能为空");
        }

        // 3. 从Redis中取出验证码
        Object redisCode = redisTemplate.opsForValue().get("REGISTER_CODE:" + email);

        // 判断Redis中是否有该验证码
        if (redisCode == null) {
            throw new BusinessException("请先发送验证码");
        }

        // 4.验证码校验————与Redis中进行比较
        if (!StrUtil.equals(redisCode.toString(), code)) {
            throw new BusinessException("请输入正确的验证码");
        }

        // 5.查询数据库是否存在该管理员，若存在则更新密码
        Admin admin = emailService.findAdminByEmail(email);
        if (admin != null) {
            adminMapper.updatePasswordById(admin.getId(),newPassword);
        } else {
            throw new BusinessException("该管理员不存在");
        }

        return "重置密码成功";
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
            admin = emailService.findAdminByEmail(account);
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
    public Admin findByTel(String tel) {
        Admin admin = adminMapper.findByTel(tel);
        return admin;
    }

    @Override
    public void updatePasswordById(Integer id, String newPassword) {
        Admin admin = adminMapper.findById(id);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        adminMapper.updatePasswordById(id, newPassword);
    }

}
