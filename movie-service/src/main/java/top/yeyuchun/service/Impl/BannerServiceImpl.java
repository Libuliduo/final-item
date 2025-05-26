package top.yeyuchun.service.Impl;

import cn.hutool.core.util.ArrayUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.yeyuchun.entity.Banner;
import top.yeyuchun.exception.BusinessException;
import top.yeyuchun.mapper.BannerMapper;
import top.yeyuchun.service.BannerService;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Override
    public List<Banner> findBannerList() {
        // 查询前5条数据
        PageHelper.startPage(1, 5);
        List<Banner> bannerList = bannerMapper.findBannerList();
        return bannerList;
    }

    @Override
    public Banner findById(Integer movieId) {
        return bannerMapper.findById(movieId);
    }

    @Override
    public void saveByMovieId(Integer movieId) {
        // 1. 检查当前轮播数量是否超过5
        Integer bannerCount = bannerMapper.countBanners();
        if (bannerCount >= 5) throw new BusinessException("轮播图已经达到上限，请删除再添加");

        // 2. 检查轮播中是否存在要添加的轮播
        if (bannerMapper.findById(movieId) != null) throw new BusinessException("该影片已经在横幅列表中，请勿重复添加");

        bannerMapper.saveByMovieId(movieId);
    }

    @Override
    public void deleteById(Integer bannerId) {
        if (bannerMapper.findById(bannerId) == null) throw new BusinessException("数据不存在，删除失败");
        bannerMapper.deleteById(bannerId);
    }

    @Override
    @Transactional
    public void deleteBatch(Integer[] ids) {
        // 使用hutool工具类进行判断
        if (ArrayUtil.isEmpty(ids)) throw new BusinessException("请选择要删除的轮播影视");
        bannerMapper.deleteBatch(ids);
    }
}
