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
        // 从结果中移除当前电影ID
        similarMovieIds.remove(currentMovieId);
        System.out.println("和本电影第一个类型一样的全部电影id（已排除当前电影）: " + similarMovieIds);

        List<Integer> finalMovieIds = new ArrayList<>();

        // 3. 如果有第二个类型，筛选同时具有两个类型的电影
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

        // 如果没有找到相似电影，返回默认推荐（同样需要排除当前电影）
        if (finalMovieIds.isEmpty()) {
            List<Movie> defaultMovies = movieService.findDefaultMovieIds();
            defaultMovies.removeIf(movie -> movie.getId().equals(currentMovieId));
            return defaultMovies;
        }

        // 4. 获取对应电影对象
        return movieService.findMoviesByIds(finalMovieIds);
    }

    // 获取推荐的电影列表
    @Override
    public List<Movie> getRecommendedMovies(Integer userId) {
        // 1. 获取目标用户的收藏电影列表
        List<Integer> targetUserMovies = userService.findMovieIdsByUserId(userId);
        System.out.println("目标用户的电影列表: " + targetUserMovies);

        // 2. 如果目标用户的收藏为空，则推荐最新的6条电影
        if (targetUserMovies.isEmpty()) {
            List<Movie> movieList = movieService.findDefaultMovieIds();
            return movieList;
        }

        // 3. 获取所有用户的收藏电影列表
        List<User> allUsers = userService.findAllUsers();
        Map<Integer, List<Integer>> userMovieMap = new HashMap<>();
        boolean hasAnyFavorites = false;
        for (User user : allUsers) {
            List<Integer> userMovies = userService.findMovieIdsByUserId(user.getId());
            if (!userMovies.isEmpty()) {
                hasAnyFavorites = true;
            }
            userMovieMap.put(user.getId(), userMovies);
        }

        // 如果没有任何用户收藏过电影，推荐最新的6条电影
        if (!hasAnyFavorites) {
            List<Movie> movieList = movieService.findDefaultMovieIds();
            return movieList;
        }

        // 4. 计算用户与其他用户的余弦相似度
        Map<Integer, Double> similarityMap = new HashMap<>();
        System.out.println("开始计算用户相似度...");
        for (Map.Entry<Integer, List<Integer>> entry : userMovieMap.entrySet()) {
            Integer otherUserId = entry.getKey();
            // 排除用户与自己进行相似度比较
            if (!otherUserId.equals(userId)) {
                List<Integer> otherUserMovies = entry.getValue();
                if (!otherUserMovies.isEmpty()) {  // 只考虑有收藏的用户
                    double similarity = calculateCosineSimilarity(targetUserMovies, otherUserMovies);
                    similarityMap.put(otherUserId, similarity);
                    System.out.println("用户" + otherUserId + "的收藏列表: " + otherUserMovies);
                    System.out.println("目标用户" + "与用户 " + otherUserId + "的余弦相似度: " + similarity);
                }
            }
        }

        // 5. 获取相似度用户喜欢的但目标用户尚未收藏的电影
        Set<Integer> recommendedMovies = new HashSet<>();
        System.out.println("计算得到的用户相似度Map: " + similarityMap);
        
        // 如果没有找到相似用户，返回默认推荐
        if (similarityMap.isEmpty() || similarityMap.values().stream().allMatch(v -> v == 0)) {
            System.out.println("没有找到相似用户，返回默认推荐");
            return movieService.findDefaultMovieIds();
        }

        // 找出最大相似度
        double maxSimilarity = similarityMap.values().stream().max(Double::compare).orElse(0.0);
        // 设定相似度阈值为最大相似度的50%
        double similarityThreshold = maxSimilarity * 0.5;
        System.out.println("最大相似度: " + maxSimilarity + ", 相似度阈值: " + similarityThreshold);

        for (Map.Entry<Integer, Double> entry : similarityMap.entrySet()) {
            Integer similarUserId = entry.getKey();
            double similarity = entry.getValue();
            // 放宽相似度条件，只要大于阈值就考虑
            if (similarity >= similarityThreshold) {
                List<Integer> similarUserMovies = userService.findMovieIdsByUserId(similarUserId);
                for (Integer movieId : similarUserMovies) {
                    if (!targetUserMovies.contains(movieId)) {
                        recommendedMovies.add(movieId);
                        System.out.println("添加推荐电影: " + movieId + " (来自用户 " + similarUserId + ", 相似度: " + similarity + ")");
                    }
                }
            }
        }

        // 如果依然没有推荐结果，返回默认推荐
        if (recommendedMovies.isEmpty()) {
            System.out.println("没有找到合适的推荐电影，返回默认推荐");
            return movieService.findDefaultMovieIds();
        }

        System.out.println("最终推荐的电影: " + recommendedMovies);

        return movieService.findMoviesByIds(new ArrayList<>(recommendedMovies));
    }
}

