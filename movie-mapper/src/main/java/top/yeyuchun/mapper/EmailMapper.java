package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.yeyuchun.entity.Admin;
import top.yeyuchun.entity.User;

@Mapper
public interface EmailMapper {

    @Select("select * from tb_admin where email = #{email}")
    Admin findAdminByEmail(String email);

    @Select("select * from tb_user where email = #{email}")
    User findUserByEmail(String email);
}
