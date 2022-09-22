package com.zmf.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmf.takeaway.common.Result;
import com.zmf.takeaway.entity.User;
import com.zmf.takeaway.service.UserService;
import com.zmf.takeaway.utils.SMSUtils;
import com.zmf.takeaway.utils.ValidateCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 翟某人~
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "前端用户登录的相关接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping("/sendMsg")
    public Result<String> sendMsg(User user, HttpServletRequest request){

        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码： {}",code);
            /*

            //发短信
            SMSUtils.sendMessage("","",phone,code);
            //将code存入session
            request.getSession().setAttribute(phone,code);

             */
            //将验证码缓存到Redis，设置存活时间五分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return Result.success("查收验证码！");
        }
        return Result.error("手机号码为空！");
    }

    /**
     * 退出登录
     * @param map
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口")
    public Result<User> login(@RequestBody Map map,HttpServletRequest request){
        log.info(" map: {}",map.toString());

        String phone = map.get("phone").toString();
        String password = map.get("password").toString();

//        log.info("pnone: {}",phone);
        log.info("password: {}",password);

//        String code = map.get("code").toString();

        //从Redis中获取验证码
//        Object code = redisTemplate.opsForValue().get(phone);

        //比较验证码


        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(User::getPhone,phone);
        User user = userService.getOne(queryWrapper);
        if (user==null){
            return Result.error("无该用户");
        }
        String pswd = user.getPassword();
        //密码不符合
        if (!password.equals(pswd)){
            return Result.error("密码错误~");
        }
//        if (user==null){
//            user = new User();
//            user.setPhone(phone);
//            user.setStatus(1);
//            userService.save(user);
//        }
        //将用户的ID存入Session
        request.getSession().setAttribute("userId",user.getId());

        //登录成功从Redis中销毁验证码
//        redisTemplate.delete(phone);

        return Result.success(user);
    }
/*
    请求网址: http://localhost:8080/user/logout
    请求方法: POST
    */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){

        //清除Session
        request.getSession().removeAttribute("userId");
        return Result.success("success~");
    }

    /*
    'url': '/user/registry',
     'method': 'post',
     data
     */

    @PostMapping("/registry")
    @ApiOperation(value = "用户登注册接口")
    public Result<String> registry(@RequestBody Map map){
        log.info("phone: {},pswd: {}"+map.get("phone"),map.get("password"));
        //得到用户电话作为账号
        String phone = map.get("phone").toString();
        //得到用户密码
        String password = map.get("password").toString();

        log.info("password: {}",password);
        log.info("password: {}",password);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(User::getPhone,phone);
        //通过电话来从数据库中查询用户
        User user = userService.getOne(queryWrapper);

        //不存在该用户
        if (user==null){
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            user.setPassword(password);
            //存入数据库
            userService.save(user);
        }
        else {
            return Result.error("该用户已存在~");
        }
        return Result.success("注册成功~");
    }
}
