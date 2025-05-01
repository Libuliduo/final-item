package top.yeyuchun.service;


import java.util.Map;

public interface UserService {
    String login(Map<String,String> paramMap);

    void verify(String token);
}
