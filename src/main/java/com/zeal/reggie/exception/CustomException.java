package com.zeal.reggie.exception;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:自定义业务异常
 * @date: 2022-06-24 23:17
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
