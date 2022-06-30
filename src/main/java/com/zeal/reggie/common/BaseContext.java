package com.zeal.reggie.common;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:基于ThreadLocal封装工具类,用来保存和获取当前登录用户id
 * @date: 2022-06-23 23:11
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
