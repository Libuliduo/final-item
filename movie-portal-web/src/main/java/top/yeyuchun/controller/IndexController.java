package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.MovieService;

import java.util.List;

@RestController
@RequestMapping("index")
public class IndexController {
    @Autowired
    private MovieService movieService;

    // 首页 查询所有电影
    @GetMapping("/movie/findAll")
    public Result findAll() {
        List<Movie> movieList = movieService.findAll();
        return Result.success(movieList);
    }

    @GetMapping("/movie/findMovieById")
    public Result findMovieById(@RequestParam Integer id) {
        Movie movie = movieService.findById(id);
        return Result.success(movie);
    }

    // 华语剧 查询地区为CN HK TW的movie
    @GetMapping("/movie/findChinese")
    public Result findChinese() {
        List<Movie> movieList = movieService.findChinese();
        return Result.success(movieList);
    }

    // 欧美剧 查询地区为 US（美国）, GB（英国）, FR（法国）, DE（德国）, IT（意大利）, IE（爱尔兰）的movie
    @GetMapping("/movie/findEnglish")
    public Result findEnglish() {
        List<Movie> movieList = movieService.findEnglish();
        return Result.success(movieList);
    }

    // 日韩剧 JP KR
    @GetMapping("/movie/findJPAndKR")
    public Result findJPAndKR() {
        List<Movie> movieList = movieService.findJPAndKR();
        return Result.success(movieList);
    }

    @GetMapping("/movie/findOther")
    public Result findOther() {
        List<Movie> movieList = movieService.findOther();
        return Result.success(movieList);
    }
}
