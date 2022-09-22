package com.zmf.takeaway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Slf4j  //提供日志
@SpringBootApplication
@ServletComponentScan   //Servlet组件扫描
@EnableTransactionManagement    //开启事务注解
@EnableCaching  //springcahce
public class TakeAwayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TakeAwayApplication.class,args);
        log.info("项目已经启动~");
    }
}
