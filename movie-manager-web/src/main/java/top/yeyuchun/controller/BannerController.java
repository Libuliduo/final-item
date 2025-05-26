package top.yeyuchun.controller;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    // 删除单个banner
    @DeleteMapping("deleteById")
    public Result deleteBanner(@RequestParam Integer bannerId) {
        bannerService.deleteById(bannerId);
        return Result.success("删除成功");
    }

    // 批量删除banner
    @DeleteMapping("deleteByIds")
    public Result deleteBatch(Integer[] ids) {
        bannerService.deleteBatch(ids);
        return Result.success("批量删除成功");
    }
}
