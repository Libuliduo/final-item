package top.yeyuchun.controller;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeyuchun.entity.Banner;
import top.yeyuchun.result.Result;
import top.yeyuchun.service.BannerService;

import java.util.List;

@RestController
@RequestMapping("banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    // 首页查询轮播影视
    @GetMapping("findBannerList")
    public Result findBannerList() {
        PageHelper.startPage(1, 5);
        List<Banner> bannerList = bannerService.findBannerList();
        return Result.success(bannerList);
    }
}
