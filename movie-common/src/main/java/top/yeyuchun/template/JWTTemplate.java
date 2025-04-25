package top.yeyuchun.template;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

//JWT的生成和解析
@Data
@Component
@ConfigurationProperties(prefix = "movies.jwt.admin")
public class JWTTemplate {

    // 管理端员工生成jwt令牌相关配置
    private String adminSecret;
    private Long adminTtl;

    // 使用JWT生成token
    public String createJWT(Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + adminTtl;
        Date exp = new Date(expMillis);

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, adminSecret.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);

        return builder.compact();
    }

    // Token 解密
    public Claims parseJWT(String token) {
        // 得到DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(adminSecret.getBytes(StandardCharsets.UTF_8))
                // 设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }

    // 取消登录验证
//    public Claims parseJWT(String token) {
//        // 打印 token，看看前端传的是什么
//        System.out.println("收到的 JWT: " + token);
//
//        // 直接返回一个假的 Claims，绕过 JWT 解析
//        Claims fakeClaims = new DefaultClaims();
//        fakeClaims.put("userId", 123);  // 模拟一个用户 ID
//        fakeClaims.put("role", "admin"); // 模拟角色
//        return fakeClaims;
//    }
}
