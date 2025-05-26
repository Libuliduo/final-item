package top.yeyuchun.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.yeyuchun.entity.Banner;


import java.util.List;

@Mapper
public interface BannerMapper {

    @Select("select * from tb_banner order by release_date desc")
    List<Banner> findBannerList();

    @Insert("insert into tb_banner (id,title,original_title,director,poster_path,url,popularity,release_date) select id,title,original_title,director,poster_path,url,popularity,release_date from tb_movie where id = #{movieId}")
    void saveByMovieId(Integer movieId);

    @Select("select * from tb_banner where id =#{movieId}")
    Banner findById(Integer movieId);

    @Select("SELECT COUNT(*) FROM tb_banner")
    Integer countBanners();

    @Delete("DELETE FROM tb_banner WHERE id = #{bannerId}")
    void deleteById(Integer bannerId);

    void deleteBatch(Integer[] ids);
}
