package org.jredis.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: 追加请求对象
 * @Author MengQingHao
 * @Date 2020/5/9 5:17 下午
 */
@Data
public class AppendRequestVO<T> {

    @NotNull
    private String key;
    @NotNull
    private T value;

}
