package top.yeyuchun.service.Impl;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeyuchun.entity.Movie;
import top.yeyuchun.mapper.BannerMapper;
import top.yeyuchun.service.BannerService;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Override
    public List<Movie> findBannerList() {
        // 查询前5条数据
        PageHelper.startPage(1, 5);
        List<Movie> bannerList = bannerMapper.findBannerList();
        return bannerList;
    }
}
