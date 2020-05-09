package org.jredis.constant.code;

/**
 * @Description: 操作缓存异常
 * @Author MengQingHao
 * @Date 2020/5/9 5:05 下午
 */
public enum OperateCacheErrorEnum implements ErrorCode {
    KEY_NOT_FIND("A0001", "not find key!"),
    ;

    private String code;
    private String message;

    OperateCacheErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
