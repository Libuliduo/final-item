package top.yeyuchun.test;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import top.yeyuchun.template.JWTTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TemplateTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testKeyValue() {
        //        1. 获取operation对象
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //        2. 往redis存值
        valueOperations.set("hello", "world");

        //        3. 从redis获取值
        Object value = valueOperations.get("hello");
        System.out.println(value);
    }

    @Test
    public void testExpire() {
        // 1. operation对象
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 2. 往redis中存值
        valueOperations.set("7722005222@qq.com", "123123", 1L, TimeUnit.MINUTES);
        valueOperations.set("3127113269@qq.com","321321", Duration.ofSeconds(20));

        // 3. 从redis中取值
        Object value = valueOperations.get("7722005222@qq.com");
        Object value2 = valueOperations.get("3127113269@qq.com");

        System.out.println(value + "--------" + value2);
    }

    @Autowired
    private JWTTemplate jwtTemplate;

    @Test
    public void createToken() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 3);
        map.put("userName", "tom");
        String token = jwtTemplate.createJWT(map);
        System.out.println(token);
    }

    // 解析JWT的token
    @Test
    public void parseToken() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiZW1haWwiOiI3NzIyMDA1MjJAcXEuY29tIiwidGVsIjoiMTMzMTIzNDEyMzQiLCJwYXNzd29yZCI6bnVsbCwiZXhwIjoxNzQ2Nzk0NDYzfQ.W-aYuAo80NuKQ5RBBP1MO2JjKxOLv5ktarWVHCH_ovw";
        Claims claims = jwtTemplate.parseJWT(token);
        System.out.println(claims);
    }

}

