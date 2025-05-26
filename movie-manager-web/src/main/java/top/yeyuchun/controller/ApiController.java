package top.yeyuchun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.ApiService;

@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired
    private ApiService apiService;

    // TMDB：通过影视名title 调用API获取影视信息
    @GetMapping("info")
    public Result getMovieInfo(@RequestParam String title) {
        System.out.println("要查询的影视名：" + title);
        Movie movie = apiService.getInfoByTMDB(title);
        return Result.success(movie);
    }

    // Alist：通过title，调用API获取播放链接
    @GetMapping("movieUrl")
    public Result getMovieUrl(@RequestParam String title) {
        System.out.println("要查询的影视名：" + title);
        String movieUrl = apiService.getUrlByAlist(title);
        return Result.success(movieUrl);
    }
}
