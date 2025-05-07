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

@Data
@Component // 让 Spring 扫描并将此类注入为 Bean（可 @Autowired 使用）
@ConfigurationProperties(prefix = "movies.jwt.admin") //Spring Boot 会自动做映射处理 将配置文件中的admin-secret 映射到定义的adminSecret
public class JWTTemplate {

    private String adminSecret; // 用于签名 JWT 的密钥
    private Long adminTtl; // Token 的生存时间（TTL，单位：毫秒）

    // 使用JWT生成token
    public String createJWT(Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        // 使用 HMAC SHA-256 签名算法对 Token 签名（常见安全算法）。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 设置过期时间
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

    // 解析 JWT Token
    public Claims parseJWT(String token) {
        // 得到DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(adminSecret.getBytes(StandardCharsets.UTF_8))
                // 设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }

}
