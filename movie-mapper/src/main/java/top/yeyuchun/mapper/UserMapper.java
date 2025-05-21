package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.*;
import top.yeyuchun.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from tb_user where id = #{id}")
    User findById(Integer id);

    @Select("select * from tb_user where email = #{email}")
    User findByEmail(String email);

    @Insert("INSERT INTO tb_user_movie (user_id, movie_id) VALUES (#{userId}, #{movieId})")
    void insertFavorite(Integer userId, Integer movieId);

    @Delete("DELETE FROM tb_user_movie WHERE user_id = #{userId} AND movie_id = #{movieId}")
    void deleteFavorite(Integer userId, Integer movieId);

    @Select("SELECT COUNT(*) FROM tb_user_movie WHERE user_id = #{userId} AND movie_id = #{movieId}")
    boolean isFavorite(Integer userId, Integer movieId);

    @Select("SELECT movie_id FROM tb_user_movie WHERE user_id = #{userId}")
    List<Integer> findMovieIdsByUserId(Integer userId);

    @Select("select * from tb_user")
    List<User> findAllUsers();

    @Insert("INSERT INTO tb_user (email, username, password) VALUES (#{email}, #{username}, #{password})")
    void save(User user);

    @Update("UPDATE tb_user SET password = #{password} WHERE email = #{email}")
    void updatePwdByEmail(String email, String password);


    @Update("UPDATE tb_user SET password = #{password} WHERE id = #{id}")
    void updatePwdById(Integer id, String password);

    @Update("UPDATE tb_user SET username = #{userName} WHERE id = #{id}")
    void resetUserName(Integer id, String userName);

    @Update("UPDATE tb_user SET avatar = #{url} WHERE id = #{id}")
    void resetUserAvatar(Integer id, String url);
}
