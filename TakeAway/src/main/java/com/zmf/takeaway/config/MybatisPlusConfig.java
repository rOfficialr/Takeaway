package com.zmf.takeaway.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 翟某人~
 * @version 1.0
 */

/**
 * 配置分页插件
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        //PaginationInnerInterceptor分页拦截器
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());

        return mybatisPlusInterceptor;
    }
}
