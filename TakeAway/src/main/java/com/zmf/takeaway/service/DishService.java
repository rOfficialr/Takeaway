package com.zmf.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmf.takeaway.dto.DishDto;
import com.zmf.takeaway.entity.Dish;

import java.util.List;

/**
 * @author 翟某人~
 * @version 1.0
 */
public interface DishService extends IService<Dish> {

    //新增菜品同时插入对应口味； 操作两张表dish,dishFlavor
    public void addDishWithFlavor(DishDto dishDto);

    //根据ID查Dish和DishFlavor
    DishDto getByIdWithFlavor(Long id);

    //根据ID修改Dish和DishFlavor
    void updateWithFlavor(DishDto dishDto);

}
