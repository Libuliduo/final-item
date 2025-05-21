package top.yeyuchun.service.Impl;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeyuchun.exception.LoginException;
import top.yeyuchun.service.TokenService;
import top.yeyuchun.template.JWTTemplate;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private JWTTemplate jwtTemplate;

    @Override
    public Integer getAdminIdByToken(String token) {
        Claims claims = jwtTemplate.parseJWT(token);
        // 提取 id 字段，并转换为 Integer 类型
        return claims.get("id", Integer.class);
    }

    @Override
    public String getAdminNameByToken(String token) {
        Claims claims = jwtTemplate.parseJWT(token);
        // 提取 name 字段，并转换成String类型
        return claims.get("name", String.class);
    }

    @Override
    public void verify(String token) {
        try {
            jwtTemplate.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LoginException();
        }
    }


}
