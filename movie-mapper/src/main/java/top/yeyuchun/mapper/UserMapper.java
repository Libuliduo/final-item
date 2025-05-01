package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.yeyuchun.entity.User;

@Mapper
public interface UserMapper {

    @Select("select * from tb_user where email = #{email}")
    User findByEmail(String email);
}
