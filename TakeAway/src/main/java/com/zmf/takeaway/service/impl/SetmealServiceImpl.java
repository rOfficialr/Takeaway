package com.zmf.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmf.takeaway.common.CustomException;
import com.zmf.takeaway.dto.SetmealDto;
import com.zmf.takeaway.entity.Category;
import com.zmf.takeaway.entity.Setmeal;
import com.zmf.takeaway.entity.SetmealDish;
import com.zmf.takeaway.mapper.CategoryMapper;
import com.zmf.takeaway.mapper.SetmealMapper;
import com.zmf.takeaway.service.CategoryService;
import com.zmf.takeaway.service.SetmealDishService;
import com.zmf.takeaway.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    //保存套餐和套餐对应的dish的关联关系
    @Override
    @Transactional
    public void addWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);

        //得到关系数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //setmealDishes对象里面没有setmealID 需要手动设置
        List<SetmealDish> setmealDishList = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //批量保存关系数据
        setmealDishService.saveBatch(setmealDishList);
    }
    //删除套餐和套餐对应的dish的关联关系
    @Transactional
    @Override
    public void deleteWithDish(List<Long> ids) {
        //查询ID在 ids中的并且 状态为1（售卖中）的套餐数据的个数；
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        //存在正在售卖的套餐
        if (count>0){
            throw new CustomException("存在套餐正在售卖无法删除！");
        }
        //删除套餐基本表的数据
        this.removeByIds(ids);


        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper();
        //构造条件，where setmealId in (ids)
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        //删除关系表的数据
        setmealDishService.remove(queryWrapper1);

    }

    /**
     * 改变状态
     * @param status
     * @param ids
     */
    @Override
    public void updateStatus(int status, List<Long> ids) {
        for (long id:ids){
            Setmeal setmeal = this.getById(id);
            setmeal.setStatus(status);
            this.updateById(setmeal);
        }

    }
}
