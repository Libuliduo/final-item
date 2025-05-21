package top.yeyuchun.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {

    private Integer code;// 状态码：1 表示成功，0 或其他值表示失败
    private String msg; //错误提示信息
    private Object data; //返回的数据

    // 构造成功返回，无数据
    public static Result success() {
        Result result = new Result();
        result.code = 1;
        return result;
    }

    // 构造成功返回，带数据
    public static Result success(Object object) {
        Result result = new Result();
        result.data = object;
        result.code = 1;
        return result;
    }

    // 构造失败返回，带错误信息
    public static Result error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }

    // 构造失败返回，带自定义错误码和提示信息
    public static  Result error(Integer code,String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = code;
        return result;
    }
}
