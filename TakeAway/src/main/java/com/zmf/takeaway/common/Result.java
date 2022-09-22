package com.zmf.takeaway.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果类
 * @param <T>
 */
@Data
@ApiModel("返回结果")
public class Result<T> implements Serializable {

    @ApiModelProperty("结果编码：1t,0f")
    private Integer code; //编码：1成功，0和其它数字为失败

    @ApiModelProperty("返回信息")
    private String msg; //错误信息

    @ApiModelProperty("返回数据")
    private T data; //数据

    private Map map = new HashMap(); //动态数据

    public static <T> Result<T> success(T object) {
        Result<T> r = new Result<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result r = new Result();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    //封装为Result对象
    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
