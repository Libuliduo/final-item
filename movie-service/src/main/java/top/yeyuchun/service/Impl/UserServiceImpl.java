package top.yeyuchun.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.entity.User;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.exception.LoginException;
import top.yeyuchun.mapper.MovieMapper;
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
    private MovieMapper movieMapper;

    @Autowired
    private JWTTemplate jwtTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public User findById(Integer id) {
        User user = userMapper.findById(id);
        return user;
    }

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
    public List<Movie> findFavorByUserId(Integer userId) {
        List<Integer> movieIds = userMapper.findMovieIdsByUserId(userId);
        List<Movie> movieList = movieMapper.findMoviesByIds(movieIds);
        return movieList;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> allUsers = userMapper.findAllUsers();
        return allUsers;
    }

    @Override
    public void updatePwdByEmail(String email, String newPwd) {
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.updatePwdByEmail(email, newPwd);
    }

    @Override
    public String resetUserPwd(Map<String, String> paramMap) {
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

        // 3.从Redis中取出验证码
        Object redisCode = redisTemplate.opsForValue().get("REGISTER_CODE:" + email);

        // 判断redis中是否有该验证码
        if (redisCode == null) {
            throw new BusinessException("请先发送验证码");
        }

        // 4.验证码校验————与redis中比较
        if (!StrUtil.equals(redisCode.toString(), code)) {
            throw new BusinessException("请输入正确的验证码");
        }

        // 5..查询数据库是否存在该用户，若存在则更新密码
        User user = userMapper.findByEmail(email);
        if (user != null) {
            userMapper.updatePwdByEmail(user.getEmail(),newPassword);
        } else {
            throw new BusinessException("该用户不存在");
        }

        return "重置密码成功";
    }

    @Override
    public void updatePasswordById(Integer id, String newPassword) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.updatePwdById(id, newPassword);
    }

    @Override
    public void resetUserName(Integer id, String userName) {
        userMapper.resetUserName(id,userName);
    }

    @Override
    public void resetUserAvatar(Integer id, String url) {
        userMapper.resetUserAvatar(id,url);
    }


    @Override
    public String registerUser(Map<String, String> paramMap) {
        // 1. 获取参数: email、username、password、验证码
        String email = paramMap.get("email");
        String username = paramMap.get("username");
        String password = paramMap.get("password");
        String code = paramMap.get("code");

        // 2. 参数校验
        if (StrUtil.isBlank(email) || StrUtil.isBlank(username)) {
            throw new BusinessException("邮箱和用户名不能为空");
        }
        else if (StrUtil.isBlank(password)) {
            throw new BusinessException("密码不能为空");
        }
        else if (StrUtil.isBlank(code)) {
            throw new BusinessException("验证码不能为空");
        }

        // 3. 从redis中取出验证码
        Object redisCode = redisTemplate.opsForValue().get("REGISTER_CODE:" + email);

        // 4. 判断redis中是否有该验证码
        if (redisCode == null) {
            throw new BusinessException("请先发送验证码");
        }

        // 5. 验证码校验————与redis中存放的进行比较
        if (!StrUtil.equals(redisCode.toString(),code)) {
            throw new BusinessException("请输入正确的验证码");
        }

        // 6.查询数据库是否存在该用户，若不存在则存入数据库
        User user = userMapper.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(password);
            // 保存该用户到数据库中

            userMapper.save(user);
        } else {
            throw new BusinessException("注册失败：该用户已存在");
        }
        return "ok";
    }
}
