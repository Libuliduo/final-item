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
import top.yeyuchun.service.EmailService;
import top.yeyuchun.service.UserService;
import top.yeyuchun.template.JWTTemplate;

import java.util.Collections;
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
    private EmailService emailService;

    @Autowired
    private RedisTemplate redisTemplate;

    // 发送注册验证码
    @Override
    public String sendRegisterCode(String email) {
        // 1.参数校验
        if (StrUtil.isBlank(email)) throw new BusinessException("邮箱不得为空");

        // 2.注册用户，需要用户不存在
        if (userMapper.findByEmail(email) != null) throw new BusinessException("用户已存在");

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
        if (userMapper.findByEmail(email) == null) throw new BusinessException("用不不存在");

        // 3.发送验证码
        emailService.sendResetCode(email);
        return "发送成功";
    }

    @Override
    public User findById(Integer id) {
        User user = userMapper.findById(id);
        return user;
    }

    @Override
    public String login(Map<String ,String> paramMap) {
        // 1. 先获取邮箱和密码
        String email = paramMap.get("email");
        String password = paramMap.get("password");

        // 2. 用huTool工具做参数校验
        if (StrUtil.isBlank(email) && StrUtil.isBlank(password)) throw new BusinessException("邮箱和密码不能为空");
        if (StrUtil.isBlank(email)) throw new BusinessException("邮箱不能为空");
        if (StrUtil.isBlank(password)) throw new BusinessException("密码不能为空");

        // 3. 准备登录工作：验证数据库中是否存在该用户
        User user = userMapper.findByEmail(email);
        if (user == null) throw new BusinessException("账号不存在");
        if (!user.getPassword().equals(password)) throw new BusinessException("密码不正确");

        /* 4. 都没有问题，用jwt生成token
        *  4.1 去除安全数据
        *  4.2 存放用户数据
        *  4.3 生成token
        * */
        user.setPassword(null);
        Map<String, Object> map = BeanUtil.beanToMap(user);
        String token = jwtTemplate.createJWT(map);

        return token;
    }

    @Override
    public void addFavorite(Integer userId, Integer movieId) {
        if (userMapper.isFavorite(userId, movieId)) throw new BusinessException("已收藏该电影");
        userMapper.insertFavorite(userId, movieId);
    }

    @Override
    public void removeFavorite(Integer userId, Integer movieId) {
        if (!userMapper.isFavorite(userId,movieId)) throw new BusinessException("并没有收藏该影视");
        userMapper.deleteFavorite(userId, movieId);
    }

    @Override
    public boolean isFavorite(Integer userId, Integer movieId) {
        return userMapper.isFavorite(userId, movieId);
    }

    @Override
    public Integer getIdByToken(String token) {
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
        if (movieIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Movie> movieList = movieMapper.findMoviesByIds(movieIds);
        return movieList;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> allUsers = userMapper.findAllUsers();
        return allUsers;
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

        // 3.从Redis中取出验证码
        Object redisCode = redisTemplate.opsForValue().get("RESET_CODE:" + email);

        // 判断redis中是否有该验证码
        if (redisCode == null) throw new BusinessException("请先发送验证码");

        // 4.验证码校验————与redis中比较
        if (!StrUtil.equals(redisCode.toString(), code)) throw new BusinessException("请输入正确的验证码");

        // 5..查询数据库是否存在该用户，若存在则更新密码
        User user = userMapper.findByEmail(email);
        if (user != null) {
            userMapper.updatePwdById(user.getId(),newPassword);
        } else {
            throw new BusinessException("该用户不存在");
        }

        return "重置密码成功";
    }

    @Override
    public void updatePwdById(Integer id, String newPassword) {
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
        if (StrUtil.isBlank(email) || StrUtil.isBlank(username)) throw new BusinessException("邮箱和用户名不能为空");
        if (StrUtil.isBlank(password)) throw new BusinessException("密码不能为空");
        if (StrUtil.isBlank(code)) throw new BusinessException("验证码不能为空");

        // 3.查看用户是否注册
        User user = userMapper.findByEmail(email);
        if (user != null) throw new BusinessException("注册失败：该账户已存在");

        // 4.用户未注册，进行注册操作
        // 4.1 从redis中取出验证码
        Object redisCode = redisTemplate.opsForValue().get("REGISTER_CODE:" + email);

        // 4.2 判断redis中是否有该验证码
        if (redisCode == null) throw new BusinessException("请先发送验证码");

        // 4.3 验证码校验————与redis中存放的进行比较
        if (!StrUtil.equals(redisCode.toString(),code)) throw new BusinessException("请输入正确的验证码");

        // 4.4 将该用户存入数据库
        user= new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        userMapper.save(user);

        return "注册成功";
    }
}
