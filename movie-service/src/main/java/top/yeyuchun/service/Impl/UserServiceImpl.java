package top.yeyuchun.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeyuchun.entity.User;
import top.yeyuchun.mapper.UserMapper;
import top.yeyuchun.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean login(User loginUser) {
        // 使用邮箱和密码查询用户
        User user = userMapper.findByEmailAndPassword(loginUser.getEmail(), loginUser.getPassword());
        return user != null; // 如果用户存在，返回true，表示登录成功
    }
}
