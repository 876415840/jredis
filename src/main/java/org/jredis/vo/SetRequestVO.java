package org.jredis.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: set请求对象
 * @Author MengQingHao
 * @Date 2020/5/9 4:21 下午
 */
@Data
public class SetRequestVO<T> {

    @NotNull
    private String key;
    @NotNull
    private T value;
    /**
     * 是否设置有效时间
     */
    @NotNull
    private Boolean timeout;
    /**
     * 超时时间(毫秒)
     */
    private Long expires;
}
