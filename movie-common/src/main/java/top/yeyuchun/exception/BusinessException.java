package top.yeyuchun.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private  Integer code;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code,String message) {
        super(message);
        this.code = code;
    }
}
