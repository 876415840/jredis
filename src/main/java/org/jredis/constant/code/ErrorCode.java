package org.jredis.constant.code;

/**
 * @Description: 异常code
 * @Author MengQingHao
 * @Date 2020/5/9 5:12 下午
 */
public interface ErrorCode {

    /**
     * 异常code
     * @return java.lang.String
     * @author MengQingHao
     * @date 2020/5/9 5:13 下午
     */
    String getCode();

    /**
     * 异常message
     * @return java.lang.String
     * @author MengQingHao
     * @date 2020/5/9 5:13 下午
     */
    String getMessage();
}
