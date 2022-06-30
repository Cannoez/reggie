package com.zeal.reggie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-22 21:37
 */
@Slf4j//LOMBOK提供
@SpringBootApplication
@ServletComponentScan
@MapperScan(basePackages = "com.zeal.reggie.model.dao")
@EnableTransactionManagement
public class ReggieApplication {
    public static void main(String[] args) {

        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功");
    }
}
