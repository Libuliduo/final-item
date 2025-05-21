package top.yeyuchun.service;

public interface TokenService {

    Integer getAdminIdByToken(String token);

    String getAdminNameByToken(String token);

    void verify(String token);
}
