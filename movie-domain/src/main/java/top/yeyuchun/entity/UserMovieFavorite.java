package top.yeyuchun.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserMovieFavorite implements Serializable {
    private Integer id; //主键
    private Integer userId; //用户id（外键）
    private Integer movieId; //电影id（外键）
}
