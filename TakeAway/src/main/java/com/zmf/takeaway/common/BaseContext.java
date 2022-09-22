package com.zmf.takeaway.common;

/**
 * @author 翟某人~
 * @version 1.0
 */

/**
 * 基于ThreadLocal封装工具类，保存获取当前登录用户的ID
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentID(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentID(){
        return threadLocal.get();
    }
}
