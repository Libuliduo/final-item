package top.yeyuchun.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeyuchun.entity.User;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.exception.LoginException;
import top.yeyuchun.mapper.UserMapper;
import top.yeyuchun.service.UserService;
import top.yeyuchun.template.JWTTemplate;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTTemplate jwtTemplate;

    @Override
    public String login(Map<String ,String> paramMap) {
        // 先获取邮箱和密码
        String email = paramMap.get("email");
        String password = paramMap.get("password");

        // 参数校验
        if (StrUtil.isBlank(email) || StrUtil.isBlank(password)) {
            throw new BusinessException("邮箱和密码不能为空");
        }

        // 验证数据库中是否存在该用户
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new BusinessException("不存在该用户");
        }

        if (!user.getPassword().equals(password)) {
            throw new BusinessException("密码不正确");
        }

        // 都没有问题，用jwt生成token
            // 去除安全数据
        user.setPassword(null);

            // 存放用户数据
        Map<String, Object> map = BeanUtil.beanToMap(user);

            // 生成token
        String token = jwtTemplate.createJWT(map);
        return token;
    }

    @Override
    public void verify(String token) {
        try {
            jwtTemplate.parseJWT(token);
            System.out.println("-------------------------------------");
            System.out.println("Received token: " + token); // 打印token，检查是否为空
            System.out.println("-------------------------------------");
        } catch (Exception e) {
            System.out.println("-----Impl-Exception-------------------------------");
            System.out.println("-----Impl-Exception-------------------");
            e.printStackTrace();
            throw new LoginException();
        }
    }


}
