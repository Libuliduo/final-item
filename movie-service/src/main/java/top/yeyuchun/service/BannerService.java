package top.yeyuchun.service;

import top.yeyuchun.entity.Banner;

import java.util.List;

public interface BannerService {
    List<Banner> findBannerList();

    Banner findById(Integer movieId);

    void saveByMovieId(Integer movieId);

    // 查询轮播表中的数量
    Integer countBanners();

    boolean deleteById(Integer bannerId);

    void deleteBatch(Integer[] ids);
}
