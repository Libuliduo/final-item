package top.yeyuchun.service;

import top.yeyuchun.entity.Movie;
import top.yeyuchun.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    User findById(Integer id);

    String login(Map<String,String> paramMap);

    String registerUser(Map<String, String> paramMap);

    void addFavorite(Integer userId, Integer movieId);

    void removeFavorite(Integer userId, Integer movieId);

    boolean isFavorite(Integer userId, Integer movieId);

    Integer getIdByToken(String token);

    List<Integer> findMovieIdsByUserId(Integer userId);

    List<User> findAllUsers();

    void updatePwdById(Integer id, String newPwd);

    String resetUserPwd(Map<String, String> paramMap);

    void resetUserName(Integer id, String userName);

    void resetUserAvatar(Integer id, String url);

    List<Movie> findFavorByUserId(Integer userId);

    String sendRegisterCode(String email);

    String sendResetCode(String email);
}
