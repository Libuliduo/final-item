package top.yeyuchun.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import top.yeyuchun.PortalApp;

@SpringBootTest(classes = PortalApp.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testKeyValue() {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        valueOperations.set("ok","yes");

        Object value = valueOperations.get("ok");
        System.out.println(value);
    }

}
