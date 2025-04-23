package top.yeyuchun.service;

import top.yeyuchun.entity.Admin;

import java.util.Map;

public interface AdminService {

    String generateCode();

    void sendEmail(String email);


    String registerAdmin(Map<String, String> paramMap);

    Admin login(Map<String, String> paramMap);
}
