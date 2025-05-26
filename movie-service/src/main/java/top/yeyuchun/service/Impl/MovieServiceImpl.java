package top.yeyuchun.service.Impl;

import cn.hutool.core.util.ArrayUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.mapper.MovieMapper;
import top.yeyuchun.service.BannerService;
import top.yeyuchun.service.MovieService;

import java.util.*;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private BannerService bannerService;

    @Override
    public List<Movie> findAll() {
        return movieMapper.findAll();
    }

    @Override
    public List<Movie> findChinese() {
        return movieMapper.findChinese();
    }

    @Override
    public List<Movie> findEnglish() {
        return movieMapper.findEnglish();
    }

    @Override
    public List<Movie> findJPAndKR() {
        return movieMapper.findJPAndKR();
    }

    @Override
    public List<Movie> findOther() {
        return movieMapper.findOther();
    }

    @Override
    public List<Movie> findMoviesByIds(List<Integer> ids) {
        // 如果ids为空或者没有元素，则返回空列表
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        // 调用movieMapper来执行SQL查询
        return movieMapper.findMoviesByIds(ids);
    }

    @Override
    public Movie findById(Integer id) {
        return movieMapper.findById(id);
    }

/* @Transactional： 用于多表操作
 *   保证一组数据库操作要么全部成功，要么全部失败。
 *   如果在事务执行过程中发生异常，所有操作都会被回滚
 * */
    @Override
    @Transactional // 表名插入主标和中间表是一个事务
    public void addMovie(Movie movie) {
        // 插入主表tb_movie，Mybatis会自动设置movie.id
        movieMapper.addMovie(movie);

        // 根据movie.genres 名称查询genre表中的对应类型的id
        List<Integer> genreIds = movieMapper.findGenreIdsByNames(movie.getGenres());

        // 插入中间表tb_movie_genre(movie_id + genre_id)
        if (genreIds.isEmpty()) throw new BusinessException("类型id数组为空");

        movieMapper.addMovieGenres(movie.getId(),genreIds);
    }

    @Override
    @Transactional
    public void updateById(Movie movie) {
        // 1.更新主表
        movieMapper.updateById(movie);

        // 2.删除旧的中间表记录
        movieMapper.deleteMovieGenres(movie.getId());

        // 3.插genre的id
        List<Integer> genreIds = movieMapper.findGenreIdsByNames(movie.getGenres());

        // 4.插入中间表
        movieMapper.insertMovieGenres(movie.getId(),genreIds);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if(id == null) throw new BusinessException("请选择要删除的影视");

        if (movieMapper.findById(id) == null) throw new BusinessException("数据不存在，删除失败");

        // 1. 先删除中间表
        movieMapper.deleteMovieGenres(id);

        movieMapper.deleteMovieFavor(id);

        // 2. 再删除主表
        movieMapper.deleteMovieById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(Integer[] ids) {
        if(ArrayUtil.isEmpty(ids)) throw new BusinessException("请选择要删除的影视");

        // 1. 先删除中间表的所有关联记录
        movieMapper.deleteMovieGenresBatch(ids);
        movieMapper.deleteMovieFavorBatch(ids);

        // 2. 再删除主表中的记录
        movieMapper.deleteMoviesBatch(ids);
    }

    @Override
    public PageInfo findByPage(Integer pageNum, Integer pageSize, String genre, String keyword) {
        if (pageNum <= 0) pageNum = 1;
        if (pageSize <= 0) pageSize = 5;
        if (pageSize > 100) pageSize = 100;

        // 去除空格
        keyword = (keyword == null ? null : keyword.trim());

        // PageHelper 启动分页（拦截第一个查询）
        PageHelper.startPage(pageNum, pageSize);
        List<Integer> ids = movieMapper.findMovieIdsByCondition(genre, keyword);

        if (ids.isEmpty()) {
            return new PageInfo<>(Collections.emptyList());
        }

        // 拿到分页信息（总记录数、页码等）
        PageInfo<Integer> idPageInfo = new PageInfo<>(ids);

        // 第二次查询：查电影详情
        List<Movie> movies = movieMapper.findMoviesByIds(ids);

        // 把分页信息复制到最终 PageInfo
        PageInfo<Movie> result = new PageInfo<>(movies);
        result.setTotal(idPageInfo.getTotal());
        result.setPageNum(idPageInfo.getPageNum());
        result.setPageSize(idPageInfo.getPageSize());
        result.setPages(idPageInfo.getPages());
        return result;
}

    // 置轮播
    @Override
    public void saveBanner(Integer movieId) {
        bannerService.saveByMovieId(movieId);
    }

    @Override
    public List<Movie> findDefaultMovieIds() {
        List<Integer> movieIds = movieMapper.findDefaultMovieIds();
        List<Movie> movieList = movieMapper.findMoviesByIds(movieIds);

        return movieList;
    }

}
