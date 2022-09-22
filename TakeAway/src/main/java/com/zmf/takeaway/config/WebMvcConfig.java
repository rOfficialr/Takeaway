package com.zmf.takeaway.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.zmf.takeaway.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Slf4j
@Configuration  //配置类   ·
@EnableSwagger2 //开启swagger文档功能
@EnableKnife4j //开启swagger文档功能
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 给静态资源放行
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        开启静态资源路径映射
        log.info("静态资源路径映射");

        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展MVC框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将对象转换器对象加入到MVC的转换器集合中 加在第一位；
        converters.add(0,messageConverter);
    }

    //swagger配置
    @Bean
    public Docket createRestAPi(){
        //文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfor())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zmf.takeaway.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfor() {
        return new ApiInfoBuilder()
                .title("社区送货服务")
                .version("1.0")
                .description("社区送货服务接口文挡")
                .build();
    }
}
