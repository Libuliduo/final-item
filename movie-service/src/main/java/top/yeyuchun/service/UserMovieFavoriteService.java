package top.yeyuchun.service;

import top.yeyuchun.entity.Movie;

import java.util.List;
import java.util.Map;

public interface UserMovieFavoriteService {
    List<Movie> getRecommendedMovies(Integer userId);

    // 计算两个用户的余弦相似度
    double calculateCosineSimilarity(List<Integer> userA, List<Integer> userB);

    List<Movie> getSimilarMovies(Integer currentMovieId);
}
