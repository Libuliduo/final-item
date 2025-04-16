package top.yeyuchun.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.MovieService;

import java.util.List;

/*
	1.声明当前类是一个controller类，项目启动的时候spring会创建该类的对象，并且将该对象放入spring容器中
	2.将该类中方法的返回值自动转成json字符串写回客户端
*/
@RestController
@RequestMapping("movie")//作为当前类中所有请求的前缀
public class MovieController {

    @Autowired
    private MovieService movieService;

    // 通过影视名title 通过API获取影视信息
    @GetMapping("info")
    public Result getMovieInfo(@RequestParam String title) {
        // 加上打印看下传进来的内容
        System.out.println("前端传入 title: " + title);
        Movie movie = movieService.getMovieInfo(title);
        return Result.success(movie);
    }

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
        if(movie.getId() == null) {
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
        return Result.success();
    }

    // 删除多条数据
    @DeleteMapping("deleteByIds")
    public Result deleteBatch(Integer[] ids) {
        movieService.deleteBatch(ids);
        return Result.success();
    }
}
