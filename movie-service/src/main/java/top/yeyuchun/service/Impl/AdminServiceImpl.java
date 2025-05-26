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
        // 1. 获取参数: email、username、password、验证码
        String email = paramMap.get("email");
        String name = paramMap.get("name");
        String password = paramMap.get("password");
        String code = paramMap.get("code");

        // 2. 参数校验
        if (StrUtil.isBlank(email) || StrUtil.isBlank(name)) throw new BusinessException("邮箱和用户名不能为空");
        if (StrUtil.isBlank(password)) throw new BusinessException("密码不能为空");
        if (StrUtil.isBlank(code)) throw new BusinessException("验证码不能为空");

        // 3.查看用户是否注册
        Admin admin = adminMapper.findByEmail(email);
        if (admin != null) throw new BusinessException("注册失败：该账户已存在");

        // 4.用户未注册，进行注册操作
        // 4.1 从redis中取出验证码
        Object redisCode = redisTemplate.opsForValue().get("REGISTER_CODE:" + email);

        // 4.2 判断redis中是否有该验证码
        if (redisCode == null) throw new BusinessException("请先发送验证码");

        // 4.3 验证码校验————与redis中存放的进行比较
        if (!StrUtil.equals(redisCode.toString(),code)) throw new BusinessException("请输入正确的验证码");


        // 4.4 将该用户存入数据库

        admin = new Admin();
        admin.setEmail(email);
        admin.setName(name);
        admin.setPassword(password);
        adminMapper.save(admin);

        return "注册成功";
    }

    @Override
    public String resetPassword(Map<String, String> paramMap) {
        // 1. 获取参数：邮箱、新密码、验证码
        String email = paramMap.get("email");
        String newPassword = paramMap.get("newPassword");
        String code = paramMap.get("code");

        // 2. 参数校验
        if (StrUtil.isBlank(email) || StrUtil.isBlank(newPassword)) throw new BusinessException("邮箱和新密码不能为空");
        if (StrUtil.isBlank(code)) throw new BusinessException("验证码不能为空");

        // 3. 从Redis中取出验证码
        Object redisCode = redisTemplate.opsForValue().get("RESET_CODE:" + email);

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

    // 发送注册验证码
    @Override
    public String sendRegisterCode(String email) {
        // 1.参数校验
        if (StrUtil.isBlank(email)) throw new BusinessException("邮箱不得为空");

        // 2.注册用户，需要用户不存在
        if (adminMapper.findByEmail(email) != null) throw new BusinessException("用户已存在");

        // 3.发送验证码
        emailService.sendRegisterCode(email);

        return "发送成功";
    }

    // 发送找回密码的验证码
    @Override
    public String sendResetCode(String email) {
        // 1.参数校验
        if (StrUtil.isBlank(email)) throw new BusinessException("邮箱不得为空");

        // 2.重置密码，需要用户存在
        if (adminMapper.findByEmail(email) == null) throw new BusinessException("用不不存在");

        // 3.发送验证码
        emailService.sendResetCode(email);
        return "发送成功";
    }

    @Override
    public String login(Map<String, String> paramMap) {
        // 1. 先获取邮箱和密码
        String email = paramMap.get("email");
        String password = paramMap.get("password");

        // 2. 用huTool工具做参数校验
        if (StrUtil.isBlank(email) && StrUtil.isBlank(password)) throw new BusinessException("邮箱和密码不能为空");
        if (StrUtil.isBlank(email)) throw new BusinessException("邮箱不能为空");
        if (StrUtil.isBlank(password)) throw new BusinessException("密码不能为空");

        // 3. 准备登录工作：验证数据库中是否存在该用户
        Admin admin = adminMapper.findByEmail(email);
        if (admin == null) throw new BusinessException("账号不存在");
        if (!admin.getPassword().equals(password)) throw new BusinessException("密码不正确");

        /* 4. 都没有问题，用jwt生成token
         *  4.1 去除安全数据
         *  4.2 存放管理员数据
         *  4.3 生成token
         * */
        admin.setPassword(null);
        Map<String, Object> map = BeanUtil.beanToMap(admin);
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
