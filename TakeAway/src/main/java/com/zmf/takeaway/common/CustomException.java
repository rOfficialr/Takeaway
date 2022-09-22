package com.zmf.takeaway.common;

/**
 * @author 翟某人~
 * @version 1.0
 */

/**
 * 自定义异常信息类
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
