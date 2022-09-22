package com.zmf.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmf.takeaway.dto.DishDto;
import com.zmf.takeaway.entity.Dish;
import com.zmf.takeaway.entity.DishFlavor;
import com.zmf.takeaway.mapper.DishMapper;
import com.zmf.takeaway.service.DishFlavorService;
import com.zmf.takeaway.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Service

public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品同时插入对应口味； 操作两张表dish,dishFlavor
     * @param dishDto
     */
    @Override
    @Transactional
    public void addDishWithFlavor(DishDto dishDto) {
        //保存dish基本信息到Dishes表中
        this.save(dishDto);
        //得到菜的ID
        Long id = dishDto.getId();
        //菜口味 dishFlavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        //前端传入dishFlavor的数据中没有ID值，手动设置
        flavors = flavors.stream().map((item)->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //保存菜口味到表 dishFlavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     *  根据ID查Dish和DishFlavor
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询dish对应的dishFlavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    /**
     * 根据ID修改Dish和DishFlavor
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表的基本数据
        this.updateById(dishDto);

        //清理Dishflavor表中数据根据dishID
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //重新插入Dishflavor数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        //设置ID
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        //插入dishFlavor
        dishFlavorService.saveBatch(flavors);
    }

}
