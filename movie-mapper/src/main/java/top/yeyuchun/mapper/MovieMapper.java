package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.yeyuchun.entity.Movie;

import java.util.List;

@Mapper
public interface MovieMapper {

    List<Movie> findAll();

    Movie findById(Integer id);

    // 添加单条movie
    void addMovie(Movie movie);
    // 通过类型名称查找类型ID
    List<Integer> findGenreIdsByNames(@Param("genres") List<String> genres);

    //插入中间表
    void addMovieGenres(Integer movieId, List<Integer> genreIds);

    void updateById(Movie movie);

    void deleteMovieGenres(Integer movieId);

    void deleteMovieById(Integer id);

    void insertMovieGenres(Integer movieId, List<Integer> genreIds);

    void deleteMoviesBatch(Integer[] ids);

    void deleteMovieGenresBatch(Integer[] movieIds);

    List<Movie> findChinese();

    List<Movie> findEnglish();

    List<Movie> findJPAndKR();

    List<Movie> findOther();

    // 返回推荐的电影列表：通过ids查询movies
    List<Movie> findMoviesByIds(List<Integer> ids);

    List<Integer> findMovieIdsByCondition(String genre, String keyword);

    // 默认推荐： 最新的前6条电影
    @Select("select id from tb_movie order by release_date desc limit 6")
    List<Integer> findDefaultMovieIds();

    @Delete("delete from tb_user_movie where movie_id = #{id}")
    void deleteMovieFavor(Integer id);

    void deleteMovieFavorBatch(Integer[] ids);

    @Select("SELECT genre_id FROM tb_movie_genre WHERE movie_id = #{movieId}")
    List<Integer> findGenreIdsByMovieId(Integer movieId);

    @Select("SELECT movie_id FROM tb_movie_genre WHERE genre_id = #{genreId}")
    List<Integer> findMovieIdsByGenreId(Integer genreId);

    @Select("SELECT * FROM tb_movie WHERE title = #{title}")
    Movie findByTitle(String title);
}

