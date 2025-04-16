package top.yeyuchun.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/*
    Serializable能让这个类对象能够被序列化。
    序列化就是把一个 Java 对象 → 变成一串二进制数据，可以用来：
    存入文件或数据库
    通过网络传输（比如微服务之间传对象）
    暂存到 Redis、MQ、缓存系统等
    而“反序列化”就是把这串数据 → 再还原成 Java 对象。
 */
/*
    @Data :自动生成 getter/setter 等
 */
@Data
public class Movie implements Serializable {
    private Integer id;

    private String title; //影视名

    private String originalTitle; //影视发行官方名——如果是外国影视一般为英文

    private List<String> genres; //影视类型

        private String country; //影视产地

    /*
     @DateTimeFormat是Spring框架中的一个注解，用于指定日期时间字段的格式。
        可以将字符串形式的日期时间数据转换为Java日期时间对象，或者将Java日期时间对象格式化为字符串。
     */
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
