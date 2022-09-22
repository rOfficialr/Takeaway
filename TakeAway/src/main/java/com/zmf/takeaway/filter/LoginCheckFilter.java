package com.zmf.takeaway.filter;

import com.alibaba.fastjson.JSON;
import com.zmf.takeaway.common.BaseContext;
import com.zmf.takeaway.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 翟某人~
 * @version 1.0
 */
//过滤器名称，拦截路径
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //向下转型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("拦截到请求：{}",request.getRequestURI());

        //不需要拦截的路径
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",    //移动端发送短信
                "/user/login",   //移动端登录
                "/user/registry",   //移动端登录
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        //得到请求路径
        String requestURI = request.getRequestURI();
        //判断路径是否是不需要拦截的
        if (check(urls,requestURI)){
            log.info("这个请求不需要处理：{}",request.getRequestURI());
            //放行
            filterChain.doFilter(request,response);
            //结束
            return;
        }


        //1. 判断employee是否登录
        Object employeeId = request.getSession().getAttribute("employeeId");
        if(request.getSession().getAttribute("employeeId") != null){
            log.info("用户ID为：{} 的用户已经登陆；",request.getSession().getAttribute("employeeId"));

            Long id = (Long) request.getSession().getAttribute("employeeId");
            //为线程中的属性中装入一个ID
            BaseContext.setCurrentID(id);

            //表示已经登陆
            filterChain.doFilter(request,response);
            return;
        }

        //2. 判断user是否登录
        Object userId = request.getSession().getAttribute("userId");
        if(userId != null){
            log.info("用户ID为：{} 的用户已经登陆；",userId);

            Long id = (Long) userId;
            //为线程中的属性中装入一个ID
            BaseContext.setCurrentID(id);

            //表示已经登陆
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户没有登陆！"+request.getRequestURI());

        //未登录则向前端返回json数据
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return;

    }

    public boolean check(String[] urls,String requestURI){
        for (String url:urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
