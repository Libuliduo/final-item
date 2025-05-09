package top.yeyuchun.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeyuchun.entity.User;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.exception.LoginException;
import top.yeyuchun.mapper.UserMapper;
import top.yeyuchun.service.UserService;
import top.yeyuchun.template.JWTTemplate;

import java.util.List;
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

        // 用huTool工具做参数校验
        if (StrUtil.isBlank(email) || StrUtil.isBlank(password)) {
            throw new BusinessException("邮箱和密码不能为空");
        }

        // 验证数据库中是否存在该用户
        User user = userMapper.findByEmail(email);
        System.out.println(user);
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new LoginException();
        }
    }

    @Override
    public void addFavorite(Integer userId, Integer movieId) {
        if (!userMapper.isFavorite(userId, movieId)) {
            userMapper.insertFavorite(userId, movieId);
        }
    }

    @Override
    public void removeFavorite(Integer userId, Integer movieId) {
        userMapper.deleteFavorite(userId, movieId);
    }

    @Override
    public boolean isFavorite(Integer userId, Integer movieId) {
        return userMapper.isFavorite(userId, movieId);
    }

    @Override
    public Integer getJwtUserId(String token) {
        try {
            Claims claims = jwtTemplate.parseJWT(token);
            Integer userId = (Integer) claims.get("id");
            return userId;
        } catch (Exception e) {
            throw new LoginException();
        }

    }

    @Override
    public List<Integer> findMovieIdsByUserId(Integer userId) {
        List<Integer> movieIds = userMapper.findMovieIdsByUserId(userId);
        return movieIds;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> allUsers = userMapper.findAllUsers();
        return allUsers;
    }


}
