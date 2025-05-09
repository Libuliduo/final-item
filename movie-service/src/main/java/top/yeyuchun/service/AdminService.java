package top.yeyuchun.service;

import top.yeyuchun.entity.Admin;

import java.util.Map;

public interface AdminService {

    void sendEmail(String email);

    String registerAdmin(Map<String, String> paramMap);

    String login(Map<String, String> paramMap);

    String generateCode();

    void verify(String token);

    Admin findByEmail(String email);

    Admin findByTel(String tel);

    Integer getAdminIdByToken(String token);

    String getAdminNameByToken(String token);

    void updatePasswordById(Integer id, String newPassword);
}
