package org.jredis.vo;

import lombok.Data;
import org.jredis.constant.code.ErrorCode;

/**
 * @Description: 响应对象
 * @Author MengQingHao
 * @Date 2020/5/9 2:25 下午
 */
@Data
public class ResponseVO<T> {

    public static final String SUCCESS_CODE = "200";

    private String code;

    private String message;

    private T t;

    public ResponseVO() {
    }

    public ResponseVO(String code, String message, T t) {
        this.code = code;
        this.message = message;
        this.t = t;
    }

    public static ResponseVO<Void> ofSuccess() {
        return new ResponseVO<>(SUCCESS_CODE, null, null);
    }

    public static <T> ResponseVO<T> ofSuccess(T t) {
        return new ResponseVO<T>(SUCCESS_CODE, null, t);
    }

    public static <T> ResponseVO<T> ofError(String code, String message) {
        return new ResponseVO<T>(code, message, null);
    }

    public static <T> ResponseVO<T> ofError(ErrorCode errorCode) {
        return new ResponseVO<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }
}
