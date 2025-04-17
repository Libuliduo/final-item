package top.yeyuchun.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {

    private Integer code; //编码：1成功，其它数字为失败
    private String msg; //错误提示信息
    private Object data; //返回的数据

    public static Result success() {
        Result result = new Result();
        result.code = 1;
        return result;
    }

    public static Result success(Object object) {
        Result result = new Result();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }

    public static  Result error(Integer code,String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = code;
        return result;
    }
}
