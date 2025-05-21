package top.yeyuchun.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class Movie implements Serializable {

    private Integer id;

    private String title; //影视名

    private String originalTitle; //影视发行官方名——如果是外国影视一般为英文

    private List<String> genres; //影视类型

    private String country; //影视产地

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate; //影视发布时间

    private String overview; //影视简介

    private String director; //导演姓名

    private String actors; // 演员

    private String backdropPath; //影视图片链接（横图）

    private String posterPath; //影视海报链接

    private String url; //影视播放链接

    private Double popularity; // 评分
}
