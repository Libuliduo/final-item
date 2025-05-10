package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.*;
import top.yeyuchun.entity.Admin;

@Mapper
public interface AdminMapper {

    @Select("select * from tb_admin where id = #{id}")
    Admin findById(Integer id);

    @Select("select * from tb_admin where email = #{email}")
    Admin findByEmail(String email);

    @Select("select * from tb_admin where tel = #{tel}")
    Admin findByTel(String tel);

    @Insert("INSERT INTO tb_admin (email, tel, password) VALUES (#{email}, #{tel}, #{password})")
    void save(Admin admin);

    @Update("UPDATE tb_admin SET password = #{password} WHERE id = #{id}")
    void updatePasswordById(Integer id, String password);
}
