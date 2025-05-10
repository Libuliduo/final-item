package top.yeyuchun.service;

import top.yeyuchun.entity.Admin;
import top.yeyuchun.entity.User;

public interface EmailService {

    void sendEmail(String email);

    String generateCode();

    // Admin
    Admin findAdminByEmail(String email);

    // User
    User findUserByEmail(String email);
}
