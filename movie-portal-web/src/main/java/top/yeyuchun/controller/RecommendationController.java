package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.UserMovieFavoriteService;

import java.util.List;


@RestController
@RequestMapping("recommendation")
public class RecommendationController {

    @Autowired
    private UserMovieFavoriteService userMovieFavoriteService;

    // 根据用户id获取推荐电影列表
    @GetMapping("getRecommendations")
    public Result getRecommendations(@RequestParam Integer userId) {
            //调用推荐算法获取电影
            List<Movie> recommendedMovies = userMovieFavoriteService.getRecommendedMovies(userId);
            return Result.success(recommendedMovies);
    }

    // 根据电影id获取相似电影
    @GetMapping("findSimilarMovies")
    public Result findSimilarMovies(@RequestParam Integer currentMovieId) {
            List<Movie> similarMovies = userMovieFavoriteService.getSimilarMovies(currentMovieId);
            return Result.success(similarMovies);
    }


}
