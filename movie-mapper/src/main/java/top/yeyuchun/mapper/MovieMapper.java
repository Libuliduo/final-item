package top.yeyuchun.mapper;

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

    List<Movie> findList(String genre, String keyword);
}
