package top.yeyuchun.service;


import top.yeyuchun.entity.User;

import java.util.Map;

public interface UserService {
    String login(Map<String,String> paramMap);

    void verify(String token);

    void addFavorite(Integer userId, Integer movieId);

    void removeFavorite(Integer userId, Integer movieId);

    boolean isFavorite(Integer userId, Integer movieId);

    Integer getJwtUserId(String token);
}
