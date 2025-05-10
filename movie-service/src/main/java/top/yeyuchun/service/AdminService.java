package top.yeyuchun.service;

import top.yeyuchun.entity.Admin;

import java.util.Map;

public interface AdminService {

    // TODO 删除
    Integer getAdminIdByToken(String token);
    String getAdminNameByToken(String token);


    String registerAdmin(Map<String, String> paramMap);

    String login(Map<String, String> paramMap);

    void verify(String token);

    Admin findByTel(String tel);

    void updatePasswordById(Integer id, String newPassword);
}
