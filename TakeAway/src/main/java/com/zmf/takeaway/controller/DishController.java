package com.zmf.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmf.takeaway.common.Result;
import com.zmf.takeaway.dto.DishDto;
import com.zmf.takeaway.entity.Category;
import com.zmf.takeaway.entity.Dish;
import com.zmf.takeaway.entity.DishFlavor;
import com.zmf.takeaway.service.CategoryService;
import com.zmf.takeaway.service.DishFlavorService;
import com.zmf.takeaway.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 翟某人~
 * @version 1.0
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    public Result<String> add(@RequestBody DishDto dishDto){
        log.info("新增菜品~： {}",dishDto.toString());

        //删除Redis中对应的分类下的数据
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        //新增菜品同时插入对应口味； 操作两张表dish,dishFlavor 自定义方法
        dishService.addDishWithFlavor(dishDto);
        return Result.success("添加成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        log.info("分页查询，page{},pageSize{},name{}",page,pageSize,name);
        Page<Dish> pageInfo = new Page(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name!=null,Dish::getName,name);
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = queryWrapper.orderByDesc(Dish::getUpdateTime);
        Page<Dish> page1 = dishService.page(pageInfo, dishLambdaQueryWrapper);

        //将dish的分页数据赋值给dishDto，忽略record字段
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"recored");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);


        return Result.success(dishDtoPage);
    }

    @GetMapping("/getById/{id}")
    public Result<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody DishDto dishDto){
        log.info("修改菜品~： {}",dishDto.toString());

        //删除Redis中对应的分类下的数据
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        //新增菜品同时插入对应口味； 操作两张表dish,dishFlavor 自定义方法
        dishService.updateWithFlavor(dishDto);

        return Result.success("修改成功");
    }


    //根据条件查询对应dish信息
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){

        List<DishDto> dishDtoList = null;
        //动态构造一个key
        String key = "dish_"+dish.getCategoryId()+"_1";

        //从Redis中获取数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果获取到了直接返回
        if (dishDtoList != null){
            return Result.success(dishDtoList);
        }

        //获取不到就查数据库，并将数据放入Redis
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查状态为1的
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map(item->{
            DishDto dishDto = new DishDto();
            //将dish的数据复制到dishDto
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null){
                //获得分类名设置给dishDto对象
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            //根据dish的id查询DishFlavor信息
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //将数据放入Redis    设置过期时间为一小时
        redisTemplate.opsForValue().set(key,dishDtoList,1, TimeUnit.HOURS);

        return Result.success(dishDtoList);
    }

    /*
    请求网址: http://localhost:8080/dish/delete?ids=1560229932825604097
请求方法: DELETE
     */
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam List<Long> ids ){
        log.info("批量删除dish");
        log.info("ids ;  {}",ids);

        for (long id :ids){
            Dish dish = dishService.getById(id);
            //动态构造一个key
            String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
            redisTemplate.delete(key);
        }

        dishService.removeByIds(ids);
        return Result.success("Success~");
    }
}
