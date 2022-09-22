package com.zmf.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmf.takeaway.common.BaseContext;
import com.zmf.takeaway.common.Result;
import com.zmf.takeaway.entity.Orders;
import com.zmf.takeaway.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

/**
 * @author 翟某人~
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;


    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return Result.success("Success！");

    }

    //请求网址: http://localhost:8080/order/userPage?page=1&pageSize=5
    //请求方法: GET
    //状态代码: 404

    @GetMapping("/userPage")
    public Result<Page> userPage(@PathParam("page")int page,@PathParam("pageSize") int pageSize){
        Long userId = BaseContext.getCurrentID();
        Page pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByAsc(Orders::getCheckoutTime);

        Page pageEnd = ordersService.page(pageInfo, queryWrapper);

        return Result.success(pageEnd);
    }

    /*
    请求网址: http://localhost:8080/order/page?page=1&pageSize=10
    请求方法: GET
     */
    @GetMapping("/page")
    public Result<Page> page(@PathParam("page")int page,@PathParam("pageSize") int pageSize){
        log.info("查询订单~");
//        Page<Orders> pageInfo = new Page<>();

//        Long userId = BaseContext.getCurrentID();
        Page<Orders> pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByAsc(Orders::getCheckoutTime);

        Page pageEnd = ordersService.page(pageInfo, queryWrapper);

        return Result.success(pageInfo);
    }
}
