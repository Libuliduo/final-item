package top.yeyuchun.service;

import top.yeyuchun.entity.Movie;

public interface ApiService {
    Movie getInfoByTMDB(String title);

    String getUrlByAlist(String title);

}
