package com.zmf.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmf.takeaway.common.CustomException;
import com.zmf.takeaway.entity.Category;
import com.zmf.takeaway.entity.Dish;
import com.zmf.takeaway.entity.Setmeal;
import com.zmf.takeaway.mapper.CategoryMapper;
import com.zmf.takeaway.service.CategoryService;
import com.zmf.takeaway.service.DishService;
import com.zmf.takeaway.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据ID删除分类，删除之前判断
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //查询条件
        LambdaQueryWrapper<Dish> qw = queryWrapper.eq(Dish::getCategoryId, id);
        //查看是否关联了dish；如果关联抛出异常
        int count1 = dishService.count(qw);
        if (count1>0){
            //
            throw new CustomException("该分类被关联了菜品");
        }

        //查看是不是关联套餐，是就抛异常
        LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper();
        //查询条件
        LambdaQueryWrapper<Setmeal> qw2 = queryWrapper2.eq(Setmeal::getCategoryId, id);
        //查看是否关联了dish；如果关联抛出异常
        int count2 = dishService.count(qw);
        if (count2>0){
            //
            throw new CustomException("该分类被关联了套餐");
        }
        //否则就正常删除
        super.removeById(id);
    }
}
