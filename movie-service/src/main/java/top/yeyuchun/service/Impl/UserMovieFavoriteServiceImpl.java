package top.yeyuchun.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.entity.User;
import top.yeyuchun.service.MovieService;
import top.yeyuchun.service.UserMovieFavoriteService;
import top.yeyuchun.service.UserService;

import java.util.*;

@Service
public class UserMovieFavoriteServiceImpl implements UserMovieFavoriteService {

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    // 计算两个用户之间的余弦相似度
    @Override
    public double calculateCosineSimilarity(List<Integer> userA, List<Integer> userB) {
        // 将两个用户的电影id转换为集合并去重
        HashSet<Integer> setA = new HashSet<>(userA);
        HashSet<Integer> setB = new HashSet<>(userB);

        // 计算点积：userA和userB的共同收藏的电影数量
        int dotProduct = 0;
        for (Integer movieId : setA) {
            if (setB.contains(movieId)) {
                dotProduct++;
            }
        }

        // 计算向量的模：每个用户的收藏数量
        double normA = Math.sqrt(setA.size());
        double normB = Math.sqrt(setB.size());

        // 防止分母为0
        if (normA == 0 || normB == 0) {
            return 0;
        }

        // 计算余弦相似度
        return dotProduct / (normA * normB);
    }

    @Override
public List<Movie> getSimilarMovies(Integer currentMovieId) {
    // 1. 获取当前电影的类型ID, 最多保留2个类型
    List<Integer> currentMovieGenres = movieService.findGenreIdsByMovieId(currentMovieId);
    if (currentMovieGenres == null || currentMovieGenres.isEmpty()) {
        System.out.println("当前电影没有类型");
        return new ArrayList<>(); 
    }

    if (currentMovieGenres.size() > 2) {
        // 最多保留2个类型
        currentMovieGenres = currentMovieGenres.subList(0, 2);
    }

    System.out.println("当前电影保留的类型ID: " + currentMovieGenres);

    // 2. 获取和本电影第一个类型一样的全部电影id
    List<Integer> similarMovieIds = movieService.findMovieIdsByGenreId(currentMovieGenres.get(0));
    System.out.println("和本电影第一个类型一样的全部电影id: " + similarMovieIds);

    List<Integer> finalMovieIds = new ArrayList<>();

    // 3. 如果有第二个类型，筛选同时具有两个类型的电影；否则返回所有 similarMovieIds 的电影
    if (currentMovieGenres.size() == 2) {
        for (Integer movieId : similarMovieIds) {
            List<Integer> movieGenres = movieService.findGenreIdsByMovieId(movieId);
            if (movieGenres.contains(currentMovieGenres.get(1))) {
                finalMovieIds.add(movieId);
            }
        }
    } else {
        finalMovieIds.addAll(similarMovieIds);
    }

    System.out.println("最终相似电影id: " + finalMovieIds);

    // 4. 获取对应电影对象
    return movieService.findMoviesByIds(finalMovieIds);
}


    // 获取推荐的电影列表
    @Override
    public List<Movie> getRecommendedMovies(Integer userId) {
        // 1. 获取目标用户的收藏电影列表
        List<Integer> targetUserMovies = userService.findMovieIdsByUserId(userId);
        System.out.println("目标用户的电影列表: " + targetUserMovies);

        // 2. 如果目标用户的收藏为空，则推荐最新的5条电影
        if (targetUserMovies.isEmpty()) {
            List<Movie> movieList = movieService.findDefaultMovieIds();
            return movieList;
        }

        // 3. 获取所有用户的收藏电影列表
        List<User> allUsers = userService.findAllUsers();
        Map<Integer, List<Integer>> userMovieMap = new HashMap<>();

        for (User user : allUsers) {
            List<Integer> userMovies = userService.findMovieIdsByUserId(user.getId());
            userMovieMap.put(user.getId(), userMovies);
        }

        // 4. 计算用户与其他用户的余弦相似度
        Map<Integer, Double> similarityMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : userMovieMap.entrySet()) {
            Integer otherUserId = entry.getKey();
            // 排除用户与自己进行相似度比较
            if (!otherUserId.equals(userId)) {
                List<Integer> otherUserMovies = entry.getValue();
                double similarity = calculateCosineSimilarity(targetUserMovies, otherUserMovies);
                similarityMap.put(otherUserId, similarity);
                System.out.println("用户" + otherUserId + "的收藏列表: " + otherUserMovies);
                System.out.println("目标用户" + "与用户 " + otherUserId + "的余弦相似度: " + similarity);
            }
        }

        // 5. 获取相似度用户喜欢的  但目标用户尚未收藏的电影
        Set<Integer> recommendedMovies = new HashSet<>();
        for (Map.Entry<Integer, Double> entry : similarityMap.entrySet()) {
            Integer similarUserId = entry.getKey();
            double similarity = entry.getValue();
            if (similarity > 0) {
                List<Integer> similarUserMovies = userService.findMovieIdsByUserId(similarUserId);
                for (Integer movieId : similarUserMovies) {
                    if (!targetUserMovies.contains(movieId)) {
                        recommendedMovies.add(movieId);
                        System.out.println("用户" + similarUserId + "收藏了目标用户" + userId + "未收藏的电影 " + movieId + "，相似度: " + similarity);
                    }
                }
            }
        }
        System.out.println("推荐的电影: " + recommendedMovies);

        return movieService.findMoviesByIds(new ArrayList<>(recommendedMovies));
    }
}
