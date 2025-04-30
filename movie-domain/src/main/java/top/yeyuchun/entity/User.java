package top.yeyuchun.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private String avatar;
}
