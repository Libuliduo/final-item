package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.yeyuchun.entity.User;

@Mapper
public interface UserMapper {
    User findByEmailAndPassword(String email, String password);
}
