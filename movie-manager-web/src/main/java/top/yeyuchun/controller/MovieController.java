package top.yeyuchun.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.MovieService;

import java.util.List;

@RestController
@RequestMapping("movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    // 查询数据库中所有影视
    @GetMapping("findAll")
    public Result findAll() {
        List<Movie> movieList = movieService.findAll();
        return Result.success(movieList);
    }

    // 查询数据库中单个影视：通过id查询
    @GetMapping("findById")
    public Result findById(@RequestParam Integer id) {
        Movie movie = movieService.findById(id);
        return Result.success(movie);
    }

    // 添加单条数据 或者 修改数据
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody Movie movie) {
        // 添加单条数据中没有id值，更新数据需要有id
        // 可以根据提供的id是否为空，判断执行添加还是更新方法
        if (movie.getId() == null) {
            // 添加单条数据
            movieService.addMovie(movie);
        } else {
            //更新数据
            movieService.updateById(movie);
        }
        return Result.success();
    }

    // 删除单条数据
    @DeleteMapping("deleteById")
    public Result deleteById(@RequestParam Integer id) {
        movieService.deleteById(id);
        return Result.success("删除成功");
    }

    // 删除多条数据
    @DeleteMapping("deleteByIds")
    public Result deleteBatch(Integer[] ids) {
        movieService.deleteBatch(ids);
        return Result.success("批量删除成功");
    }

    // 分页查询
    @GetMapping("list")
    public Result findByPage(
            // 传入当前页码和每页显示的条数，按需传入类型和关键字
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            String genre,
            String keyword) {
        PageInfo pageInfo = movieService.findByPage(pageNum, pageSize, genre, keyword);
        return Result.success(pageInfo);
    }

    @GetMapping("findMoviesByIds")
    public Result findMoviesByIds(@RequestParam List<Integer> ids) {
        List<Movie> movies = movieService.findMoviesByIds(ids);
        return Result.success(movies);
    }

    // 置轮播
    @PostMapping("/banner/saveBanner")
    public Result saveBanner(@RequestParam Integer movieId) {
        movieService.saveBanner(movieId);
        return Result.success("置轮播成功");
    }
}
