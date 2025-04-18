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

    @GetMapping("findAll")
    public Result findAll() {
        List<Movie> movieList = movieService.findAll();
        System.out.println("查询到的电影数据：");
        for (Movie movie : movieList) {
            System.out.println(movie.getId() + movie.getTitle());
        }
        return Result.success(movieList);
    }

    @GetMapping("findMovieById")
    public Result findMovieById(@RequestParam Integer id) {
            Movie movie = movieService.findById(id);
            return Result.success(movie);
    }
}
