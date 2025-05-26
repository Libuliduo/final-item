package top.yeyuchun.service;

import com.github.pagehelper.PageInfo;
import top.yeyuchun.entity.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> findAll();

    List<Movie> findChinese();

    Movie findById(Integer id);

    void addMovie(Movie movie);

    void updateById(Movie movie);

    void deleteById(Integer id);

    void deleteBatch(Integer[] ids);

    PageInfo findByPage(Integer pageNum, Integer pageSize, String genre, String keyword);

    List<Movie> findEnglish();

    List<Movie> findJPAndKR();

    List<Movie> findOther();

    List<Movie> findMoviesByIds(List<Integer> ids);

    void saveBanner(Integer movieId);

    List<Movie> findDefaultMovieIds();
}
