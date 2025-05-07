package top.yeyuchun.service;

import java.util.Map;

public interface AdminService {

    void sendEmail(String email);

    String registerAdmin(Map<String, String> paramMap);

    String login(Map<String, String> paramMap);

    String generateCode();

    void verify(String token);
}
