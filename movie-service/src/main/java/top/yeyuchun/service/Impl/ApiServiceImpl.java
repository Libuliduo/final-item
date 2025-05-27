package top.yeyuchun.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.service.ApiService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeoutException;

@Service
public class ApiServiceImpl implements ApiService {
    // 从配置文件中读取 TMDB 和 AList 的 API Key
    @Value("${tmdb.tmdb-api-key}")
    private String TMDBApiKey;

    @Value("${alist.alist-api-key}")
    private String AListApiKey;

    private final WebClient alistClient;

    private final WebClient tmdbClient;

    private int tempMovieId = -1;

    // 使用构造方法初始化WebClient实例并为TMDBWebClient配置代理
    public ApiServiceImpl() {
        this.alistClient = WebClient.builder()
                .baseUrl("https://alist.yeyuchun.top:23333")
                .defaultHeader("Authorization", AListApiKey)
                .build();

        this.tmdbClient = WebClient.builder()
                .baseUrl("https://api.themoviedb.org/3")
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create().proxy(proxy -> proxy
                                        .type(ProxyProvider.Proxy.HTTP)
                                        .host("127.0.0.1")
                                        .port(7890)
                                )
                        )
                )
                .build();
    }

    @Override
    public Movie getInfoByTMDB(String title) {
        if (title.isBlank()) throw new BusinessException("请输入电影名");

        Movie movie = new Movie();
        System.out.println("TMBD----1.请求基础信息");

        // 1.调用TMDB接口，获取影视基础信息
        String tmdbResponse = tmdbClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/search/movie")
                    .queryParam("api_key", TMDBApiKey)
                    .queryParam("query", title)
                    .queryParam("language", "zh-CN")
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, e-> {
                    throw new BusinessException("TMDB请求失败：" + e.getStatusCode());
                })
                .onErrorResume(TimeoutException.class, e-> {
                    //处理超时异常
                    throw new BusinessException("TMDB请求超时");
                })
                .block();

        JSONObject tmdbJson = JSON.parseObject(tmdbResponse);
        JSONArray results = tmdbJson.getJSONArray("results");

        if (results != null && !results.isEmpty()) {
            // 保存查询到的第一条数据
            JSONObject first = results.getJSONObject(0);
            tempMovieId = first.getInteger("id");

            // 设置电影基本信息
            movie.setTitle(first.getString("title"));
            movie.setOriginalTitle(first.getString("original_title"));
            movie.setOverview(first.getString("overview"));
            movie.setBackdropPath("https://image.tmdb.org/t/p/w500" + first.getString("backdrop_path"));
            movie.setPosterPath("https://image.tmdb.org/t/p/w500" + first.getString("poster_path"));

            movie.setPopularity(first.getDouble("popularity"));
            movie.setCountry(first.getString("original_language"));
            String dateStr = first.getString("release_date");
            if (dateStr != null && !dateStr.isEmpty()) {
                movie.setReleaseDate(Date.valueOf(dateStr));
            }
        } else {
            throw new BusinessException("TMDB：没有查到影视信息");
        }

        System.out.println("TMBD----2.请求详细信息");
        // 2.基础的影视信息已经获取到了，接下来通过tmdb的影视id查询国家、类型信息
        String detailResponse = tmdbClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tempMovieId)
                        .queryParam("api_key", TMDBApiKey)
                        .queryParam("language", "zh-CN")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, e-> {
                    throw new BusinessException("TMDB请求失败：" + e.getStatusCode());
                })
                .onErrorResume(TimeoutException.class, e-> {
                    //处理超时异常
                    throw new BusinessException("TMDB请求超时");
                })
                .block();

        // 2.1 查找其他信息-国家original_country
        JSONObject detailJson = JSON.parseObject(detailResponse);
        JSONArray originCountryArray = detailJson.getJSONArray("origin_country");
        if (originCountryArray == null || originCountryArray.isEmpty()) {
            throw new BusinessException("TMDB: 获取影视地区信息失败");
        }

        // 2.2 查找其他信息-影视类型
        JSONArray genresArray = detailJson.getJSONArray("genres");
        List<String> genreList = new ArrayList<>();
        if (genresArray == null || genresArray.isEmpty()) {
            throw new BusinessException("TMDB：获取影视类型失败");
        }
        for (int i = 0; i < genresArray.size(); i++) {
            String genreName = genresArray.getJSONObject(i).getString("name");
            genreList.add(genreName);
        }

        movie.setGenres(genreList);

        System.out.println("TMBD----3.请求演员信息");
        // 3. 获取导演和演员
        String creditsResponse = tmdbClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tempMovieId + "/credits")
                        .queryParam("api_key", TMDBApiKey)
                        .queryParam("language", "zh-CN")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, e-> {
                    throw new BusinessException("TMDB请求失败：" + e.getStatusCode());
                })
                .onErrorResume(TimeoutException.class, e-> {
                    //处理超时异常
                    throw new BusinessException("TMDB请求超时");
                })
                .block();

        JSONObject creditsJson = JSON.parseObject(creditsResponse);
        JSONArray crewArray = creditsJson.getJSONArray("crew"); // 工作人员
        JSONArray castArray = creditsJson.getJSONArray("cast"); // 演员列表

        // 3.1 查找导演
        if (crewArray == null) {
            throw new BusinessException("TMDB：获取导演信息失败");
        }
        for (int i = 0; i < crewArray.size(); i++) {
            JSONObject crew = crewArray.getJSONObject(i);
            if ("Director".equals(crew.getString("job"))) {
                movie.setDirector(crew.getString("name"));
                break;
            }
        }

        // 3.2 查找演员：获取前五名演员并去重
        if (castArray == null) {
            throw new BusinessException("TMDB：获取演员信息失败");
        }
        Set<String> actorsSet = new LinkedHashSet<>();
        for (int i = 0; i < castArray.size(); i++) {
            String actorName = castArray.getJSONObject(i).getString("name");
            if (actorsSet.add(actorName) && actorsSet.size() == 5) break;
        }
        movie.setActors(String.join(", ", actorsSet));

        System.out.println("TMBD----4.获取成功");
        return movie;
    }

    @Override
    public String getUrlByAlist(String title) {
        if (title.isBlank()) throw new BusinessException("请输入电影名");

        // 1. 处理影视名：去除空格
        String keywords = title.trim();

        // 1. 创建请求体
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("parent", "/NASVideo");
        searchBody.put("keywords",keywords);
        searchBody.put("scope", 2); // 0-全部 1-文件夹 2-文件
        searchBody.put("page", 1); // 页数-必需
        searchBody.put("per_page", 1); // 每页数目-必需

        // 2. 发送请求
        String alistResponse = alistClient.post()
                .uri("https://alist.yeyuchun.top:23333/api/fs/search")
                .header(HttpHeaders.AUTHORIZATION, AListApiKey)
                .bodyValue(searchBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, e-> {
                    throw new BusinessException("Alist请求失败：" + e.getStatusCode());
                })
                .onErrorResume(TimeoutException.class, e-> {
                    //处理超时异常
                    throw new BusinessException("Alist请求超时");
                })
                .block();

        // 3.处理接收到的响应
        // 3.1 用fastjson解析响应字符串
        JSONObject alistJson = JSON.parseObject(alistResponse);

        // 3.2 判断是否找到相关资源
        Integer code =  alistJson.getInteger("code");
        if (code == 401) {
            throw new BusinessException("Alist：token过期");
        }

        Integer total = alistJson.getJSONObject("data").getInteger("total");
        if (total == 0) {
            throw new BusinessException("影视库中不存在该资源");
        }
        // 3.3 获取影视在alist里面的名字name
        JSONArray contentArray = alistJson.getJSONObject("data").getJSONArray("content");
        JSONObject first = contentArray.getJSONObject(0);
        String name = first.getString("name");

        // 3.4 将名字进行URL编码与域名拼接
        String baseUrl = "https://alist.yeyuchun.top:23333/d/NASVideo/";
        String encodeName = URLEncoder.encode(name, StandardCharsets.UTF_8);

        String url = baseUrl + encodeName;
        System.out.println("Alist：获取到的播放链接为：" + url);
        return url;
    }

}
