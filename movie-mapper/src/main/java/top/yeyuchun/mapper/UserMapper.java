package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.yeyuchun.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {

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
}
