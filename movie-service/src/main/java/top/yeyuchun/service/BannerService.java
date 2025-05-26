package top.yeyuchun.service;

import top.yeyuchun.entity.Banner;

import java.util.List;

public interface BannerService {
    List<Banner> findBannerList();

    Banner findById(Integer movieId);

    void saveByMovieId(Integer movieId);

    void deleteById(Integer bannerId);

    void deleteBatch(Integer[] ids);
}
