package com.zmf.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmf.takeaway.common.BaseContext;
import com.zmf.takeaway.common.Result;
import com.zmf.takeaway.entity.ShoppingCart;
import com.zmf.takeaway.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 翟某人~
 * @version 1.0
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    //向购物车添加
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        Long id = BaseContext.getCurrentID();
        shoppingCart.setUserId(id);
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,id);

        if (dishId!=null){
            //加菜
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //加套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());

        }
        ShoppingCart shoppingOne = shoppingCartService.getOne(queryWrapper);
        //已经存在 数量加一
        if (shoppingOne!=null){
            Integer number = shoppingOne.getNumber();
            shoppingOne.setNumber(number+1);
            shoppingCartService.updateById(shoppingOne);

            return Result.success(shoppingOne);
        }else {
            //不存在则添加
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);

            return Result.success(shoppingCart);
        }

    }

    //减少数量
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart){
        //得到用户ID
        Long id = BaseContext.getCurrentID();
        //从传来的数据得到dishid
        Long dishId = shoppingCart.getDishId();
        //从传来的数据得到setmealId
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,id);
        //dish存在
        if (dishId!=null){
            //减菜
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }
        //setmeal存在
        if (setmealId!=null){
            //减套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);

        }
        //select * from shoping_cart where userId = ? and dishId = ?
        //select * from shoping_cart where userId = ? and setmealId = ?
        ShoppingCart shoppingOne = shoppingCartService.getOne(queryWrapper);

        //菜的数量或者套餐数等于一的话就删除
        if (shoppingOne.getNumber() == 1){
            shoppingCartService.remove(queryWrapper);
            return Result.success("success!");
        }
        //数量大于2 数量加一
        Integer number = shoppingOne.getNumber();
        shoppingOne.setNumber(number-1);
        shoppingCartService.updateById(shoppingOne);

        return Result.success("success");
    }


    //查询用户购物车
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        Long currentID = BaseContext.getCurrentID();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentID);
        //查询
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lambdaQueryWrapper);

        return Result.success(shoppingCartList);
    }

    @DeleteMapping("/clean")
    public Result<String> clean(){
        Long currentID = BaseContext.getCurrentID();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId,currentID);

        shoppingCartService.remove(queryWrapper);
        return Result.success("Success!");
    }
}
