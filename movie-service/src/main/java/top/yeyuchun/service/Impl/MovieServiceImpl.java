package top.yeyuchun.service.Impl;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.mapper.MovieMapper;
import top.yeyuchun.service.BannerService;
import top.yeyuchun.service.MovieService;
import top.yeyuchun.service.UserService;

import java.sql.Date;
import java.util.*;


/* Alist 获取raw_url的流程：
    1.先获取文件所在目录path
    发送post请求到https://alist.yeyuchun.top:23333/api/fs/search
    Headers里面需要Authorization，body里面需要json类型的数据
    如：{
          "parent": "/NASVideo",
          "keywords": "星际穿越",
          "scope": 2,
          "page": 1,
          "per_page": 1,
          "password": ""
        }
    得到响应，若data.content.is_dir = false，则直接找到文件了，将data.content.parent和name拼接即为path

    2.通过path查询文件链接
    发送Post请求到https://alist.yeyuchun.top:23333/api/fs/get
    Headers里面需要Authorization，body里面需要json类型的path：文件路径得到data.raw_url

 */
@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private BannerService bannerService;

    // 从配置文件中读取 TMDB 和 AList 的 API Key
    @Value("${tmdb.tmdb-api-key}")
    private String TMDBApiKey;

    @Value("${alist.alist-api-key}")
    private String AListApiKey;

    private final WebClient alistClient;

    private final WebClient tmdbClient;

    // 使用构造方法初始化WebClient实例并为TMDBWebClient配置代理
    public MovieServiceImpl() {
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
    public Movie getMovieInfo(String title) {
        Movie movie = new Movie();
        try {
            // ======= 1.调用TMDB接口，获取电影基础信息 =======
            String tmdbResponse = tmdbClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/movie")
                            .queryParam("api_key", TMDBApiKey)
                            .queryParam("query", title)
                            .queryParam("language", "zh-CN")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject tmdbJson = JSON.parseObject(tmdbResponse);
            JSONArray results = tmdbJson.getJSONArray("results");

            int tempMovieId = -1;
            if (results != null && !results.isEmpty()) {
                // 保存查询到的第一条数据
                JSONObject first = results.getJSONObject(0);
                System.out.println("TMDB响应：" + first);
                tempMovieId = first.getInteger("id");
                // 设置电影基本信息
                movie.setTitle(first.getString("title"));
                movie.setOriginalTitle(first.getString("original_title"));
                movie.setOverview(first.getString("overview"));
                movie.setBackdropPath("https://image.tmdb.org/t/p/w500" + first.getString("backdrop_path"));
                movie.setPosterPath("https://image.tmdb.org/t/p/w500" + first.getString("poster_path"));
                movie.setPopularity(first.getDouble("popularity"));
                movie.setCountry(first.getString("original_language")); // 原语言暂用国家字段
                String dateStr = first.getString("release_date");
                if (dateStr != null && !dateStr.isEmpty()) {
                    movie.setReleaseDate(Date.valueOf(dateStr));
                }
            }

            // 获取到的TMDB中的影视id
            final int movieIdFinal = tempMovieId;

            // ======= 2.获取更详细的信息:国家、影视类型） =======
            // 通过TMDB的id查询original_country并保存到movie.country中
            // 通过TMDB的id查询genres 影视类型存放到movie.genreList中
            String detailResponse = tmdbClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/" + movieIdFinal)
                            .queryParam("api_key", TMDBApiKey)
                            .queryParam("language", "zh-CN")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 设置国家original_country
            System.out.println("Detail响应：" + detailResponse);
            JSONObject detailJson = JSON.parseObject(detailResponse);
            JSONArray originCountryArray = detailJson.getJSONArray("origin_country");
            if (originCountryArray != null && !originCountryArray.isEmpty()) {
                movie.setCountry(originCountryArray.getString(0)); // 取第一个国家
            }
            // 设置类型genres
            JSONArray genresArray = detailJson.getJSONArray("genres");
            List<String> genreList = new ArrayList<>();
            if (genresArray != null) {
                for (int i = 0; i < genresArray.size(); i++) {
                    String genreName = genresArray.getJSONObject(i).getString("name");
                    genreList.add(genreName);
                }
            }
            movie.setGenres(genreList);


            // ======= 3.通过TMDB的id获取导演和演员 =======
            String creditsResponse = tmdbClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/" + movieIdFinal + "/credits")
                            .queryParam("api_key", TMDBApiKey)
                            .queryParam("language", "zh-CN")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject creditsJson = JSON.parseObject(creditsResponse);
            JSONArray crewArray = creditsJson.getJSONArray("crew"); // 工作人员
            JSONArray castArray = creditsJson.getJSONArray("cast"); // 演员列表

            // 查找导演
            if (crewArray != null) {
                for (int i = 0; i < crewArray.size(); i++) {
                    JSONObject crew = crewArray.getJSONObject(i);
                    if ("Director".equals(crew.getString("job"))) {
                        movie.setDirector(crew.getString("name"));
                        break;
                    }
                }
            }

            // 获取前5名演员 去重
            Set<String> actorsSet = new LinkedHashSet<>();
            if (castArray != null) {
                for (int i = 0; i < castArray.size(); i++) {
                    String actorName = castArray.getJSONObject(i).getString("name");
                    if (actorsSet.add(actorName) && actorsSet.size() == 5) break;
                }
                movie.setActors(String.join(", ", actorsSet));
            }

            // ======= 4.通过 AList 获取视频的Path =======
            // 将影视名用空格隔开，这样通过api可以直接搜到文件，不然会搜到目录
            String keywords = title.replaceAll("(.)", "$1 ");  // 使用正则表达式插入空格
            String tempOriginalTitle = movie.getOriginalTitle();
            String OriginalKeywords = tempOriginalTitle.replaceAll("(.)", "$1 ");

            System.out.println("keyword：" + keywords);
            String tempPath = "";
            // 创建请求体
            Map<String, Object> searchBody = new HashMap<>();
            searchBody.put("parent", "/");
            searchBody.put("keywords", keywords);
            searchBody.put("scope", 0);
            searchBody.put("page", 1);
            searchBody.put("per_page", 1);

            System.out.println("请求体：" + JSON.toJSONString(searchBody));
            // 发送请求
            String searchResponse = alistClient.post()
                    .uri("https://alist.yeyuchun.top:23333/api/fs/search")
                    .header(HttpHeaders.AUTHORIZATION, AListApiKey)
                    .bodyValue(searchBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();


            // 输出响应
            System.out.println("通过title查询到的响应：" + searchResponse);

            // 无论成功是否都会返回code=200，但是成功了content是不为空的
            JSONObject searchJson = JSON.parseObject(searchResponse);
            // 检查返回结果
            JSONObject data = searchJson.getJSONObject("data");
            if(data != null) {
                JSONArray content = data.getJSONArray("content");
                if (content != null && !content.isEmpty()) {
                    JSONObject fileInfo = content.getJSONObject(0);
                    if (!fileInfo.getBoolean("is_dir")) {
                        // 拼接path
                            String parent = fileInfo.getString("parent");
                            String name = fileInfo.getString("name");
                            tempPath = parent + "/" + name; // 拼接得到路径
                    }
                }
            }

            // 如果第一次没找到，再用 originalKeywords 搜一次
            if (tempPath.isEmpty()) {
                System.out.println("使用 title 未找到，尝试用 OriginalKeywords 再次搜索...");
                searchBody.put("keywords", OriginalKeywords);  // 修改关键词再发一次

                String secondResponse = alistClient.post()
                        .uri("https://alist.yeyuchun.top:23333/api/fs/search")
                        .header(HttpHeaders.AUTHORIZATION, AListApiKey)
                        .bodyValue(searchBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                System.out.println("通过 originalTitle 查询到的响应：" + secondResponse);

                JSONObject secondJson = JSON.parseObject(secondResponse);
                JSONObject secondData = secondJson.getJSONObject("data");
                if (secondData != null) {
                    JSONArray content = secondData.getJSONArray("content");
                    if (content != null && !content.isEmpty()) {
                        JSONObject fileInfo = content.getJSONObject(0);
                        if (!fileInfo.getBoolean("is_dir")) {
                            String parent = fileInfo.getString("parent");
                            String name = fileInfo.getString("name");
                            tempPath = parent + "/" + name;
                        }
                    }
                }
            }


            final String path = tempPath;

            // ======= 2.通过Path获取视频的 URL =======
            Map<String, String> body = new HashMap<>();
            body.put("path", path);

            String alistResponse = alistClient.post()
                    .uri("/api/fs/get")
                    .header(HttpHeaders.AUTHORIZATION, AListApiKey) // 加上 Authorization
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 解析AList响应，提取raw_url
            System.out.println("收到响应：" + alistResponse);
            // 用fastjson 解析响应字符串
            JSONObject alistJson = JSON.parseObject(alistResponse);
            // 获取data.raw_url 字段
            String rawUrl = alistJson.getJSONObject("data").getString("raw_url");
            System.out.println("获取到的rawUrl：" + rawUrl);
            movie.setUrl(rawUrl);

        } catch (Exception e) {
            throw new BusinessException("获取影视信失败");
        }

        return movie;
    }

    @Override
    public List<Movie> findAll() {
        return movieMapper.findAll();
    }

    @Override
    public List<Movie> findChinese() {
        return movieMapper.findChinese();
    }

    @Override
    public List<Movie> findEnglish() {
        return movieMapper.findEnglish();
    }

    @Override
    public List<Movie> findJPAndKR() {
        return movieMapper.findJPAndKR();
    }

    @Override
    public List<Movie> findOther() {
        return movieMapper.findOther();
    }

    @Override
    public List<Movie> findMoviesByIds(List<Integer> ids) {
        // 如果ids为空或者没有元素，则返回空列表
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        // 调用movieMapper来执行SQL查询
        return movieMapper.findMoviesByIds(ids);
    }

    @Override
    public Movie findById(Integer id) {
        return movieMapper.findById(id);
    }

/* @Transactional： 用于多表操作
 *   保证一组数据库操作要么全部成功，要么全部失败。
 *   如果在事务执行过程中发生异常，所有操作都会被回滚
 * */
    @Override
    @Transactional // 表名插入主标和中间表是一个事务
    public void addMovie(Movie movie) {
        // 插入主表tb_movie，Mybatis会自动设置movie.id
        movieMapper.addMovie(movie);

        // 根据movie.genres 名称查询genre表中的对应类型的id
        List<Integer> genreIds = movieMapper.findGenreIdsByNames(movie.getGenres());

        // 插入中间表tb_movie_genre(movie_id + genre_id)
        if(!genreIds.isEmpty()) {
            movieMapper.addMovieGenres(movie.getId(),genreIds);
        }
    }

    @Override
    @Transactional
    public void updateById(Movie movie) {
        // 1.更新主表
        movieMapper.updateById(movie);

        // 2.删除旧的中间表记录
        movieMapper.deleteMovieGenres(movie.getId());

        // 3.插genre的id
        List<Integer> genreIds = movieMapper.findGenreIdsByNames(movie.getGenres());

        // 4.插入中间表
        movieMapper.insertMovieGenres(movie.getId(),genreIds);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if(id == null) {
            throw new BusinessException("请选择要删除的影视");
        }
        // 1. 先删除中间表
        movieMapper.deleteMovieGenres(id);

        movieMapper.deleteMovieFavor(id);

        // 2. 再删除主表
        movieMapper.deleteMovieById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(Integer[] ids) {
        if(ArrayUtil.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的影视");
        }
        // 1. 先删除中间表的所有关联记录
        movieMapper.deleteMovieGenresBatch(ids);
        movieMapper.deleteMovieFavorBatch(ids);

        // 2. 再删除主表中的记录
        movieMapper.deleteMoviesBatch(ids);
    }

    @Override
    public PageInfo findByPage(Integer pageNum, Integer pageSize, String genre, String keyword) {
        if (pageNum <= 0) pageNum = 1;
        if (pageSize <= 0) pageSize = 5;
        if (pageSize > 100) pageSize = 100;


        // 去除空格
        keyword = (keyword == null ? null : keyword.trim());
        try {
            // PageHelper 启动分页（拦截第一个查询）
            PageHelper.startPage(pageNum, pageSize);
            List<Integer> ids = movieMapper.findMovieIdsByCondition(genre, keyword);

            if (ids.isEmpty()) {
                return new PageInfo<>(Collections.emptyList());
            }

            // 拿到分页信息（总记录数、页码等）
            PageInfo<Integer> idPageInfo = new PageInfo<>(ids);

            // 第二次查询：查电影详情
            List<Movie> movies = movieMapper.findMoviesByIds(ids);

            // 把分页信息复制到最终 PageInfo
            PageInfo<Movie> result = new PageInfo<>(movies);
            result.setTotal(idPageInfo.getTotal());
            result.setPageNum(idPageInfo.getPageNum());
            result.setPageSize(idPageInfo.getPageSize());
            result.setPages(idPageInfo.getPages());
            return result;
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    // 置轮播
    @Override
    public void saveBanner(Integer movieId) {
        bannerService.saveByMovieId(movieId);
    }

    @Override
    public List<Movie> findDefaultMovieIds() {
        List<Integer> movieIds = movieMapper.findDefaultMovieIds();
        List<Movie> movieList = movieMapper.findMoviesByIds(movieIds);

        return movieList;
    }

}
