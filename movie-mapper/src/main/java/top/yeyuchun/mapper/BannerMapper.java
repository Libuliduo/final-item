package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.yeyuchun.entity.Movie;

import java.util.List;

@Mapper
public interface BannerMapper {
    List<Movie> findBannerList();
}
