package top.yeyuchun.service;

import top.yeyuchun.entity.Admin;
import top.yeyuchun.entity.User;

public interface EmailService {

    String generateCode();

    void sendRegisterCode(String email);

    void sendResetCode(String email);

    // Admin
    Admin findAdminByEmail(String email);

    // User
    User findUserByEmail(String email);
}
