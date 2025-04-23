package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.yeyuchun.entity.Admin;

@Mapper
public interface AdminMapper {
    Admin findByEmail(String email);

    Admin findByTel(String tel);

    void save(Admin admin);
}
