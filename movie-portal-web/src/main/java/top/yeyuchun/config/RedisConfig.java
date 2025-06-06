package top.yeyuchun.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        //设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置value的序列化方式--将value转成json字符串存入redis
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        //设置hash中字段的序列化方式
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        //设置hash中value的序列化方式
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        return redisTemplate;
    }
}