package top.yeyuchun.service;

import top.yeyuchun.entity.Admin;

import java.util.Map;

public interface AdminService {

    String registerAdmin(Map<String, String> paramMap);

    String login(Map<String, String> paramMap);

    Admin findByTel(String tel);

    void updatePasswordById(Integer id, String newPassword);

    String resetPassword(Map<String, String> paramMap);
}
