package top.yeyuchun.service;

import top.yeyuchun.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    String login(Map<String,String> paramMap);

    void verify(String token);

    void addFavorite(Integer userId, Integer movieId);

    void removeFavorite(Integer userId, Integer movieId);

    boolean isFavorite(Integer userId, Integer movieId);

    Integer getJwtUserId(String token);

    List<Integer> findMovieIdsByUserId(Integer userId);

    // 获取所有用户
    List<User> findAllUsers();

    String registerUser(Map<String, String> paramMap);
}
