package com.zmf.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmf.takeaway.common.Result;
import com.zmf.takeaway.dto.SetmealDto;
import com.zmf.takeaway.entity.Category;
import com.zmf.takeaway.entity.Setmeal;
import com.zmf.takeaway.entity.SetmealDish;
import com.zmf.takeaway.service.CategoryService;
import com.zmf.takeaway.service.SetmealDishService;
import com.zmf.takeaway.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 翟某人~
 * @version 1.0
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    //添加套餐
    @RequestMapping("/add")
    public Result<String> add(@RequestBody SetmealDto setmealDto){
        setmealService.addWithDish(setmealDto);

        String key = "setmeal_"+setmealDto.getCategoryId()+"_"+setmealDto.getStatus();
        //删除Redis数据
        redisTemplate.delete(key);

        return Result.success("添加成功~");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>();  //次page对象里面咩有套餐分类信息
        Page<SetmealDto> dtoPage = new Page<>();    //构造一个新的

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //根据更新时间降序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //查询得到分页结果
        Page<Setmeal> setmealPage = setmealService.page(pageInfo, queryWrapper);
        //将结果复制给dtoPage对象 排除records属性，此属性单独封装；
        BeanUtils.copyProperties(setmealPage,dtoPage,"records");
        //得到setmealPage的records的值
        List<Setmeal> records = setmealPage.getRecords();


        //遍历 setmealPage的records的值
        List<SetmealDto> dtoList = records.stream().map((item) -> {
            //每次遍历都要new一个setmealDto对象存放对应的数据
            SetmealDto setmealDto = new SetmealDto();
            //将对象拷贝到setmealDto
            BeanUtils.copyProperties(item, setmealDto);
            //得到分类ID
            Long categoryId = item.getCategoryId();
            //通过分类ID得到分类实体
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //得到分类名称
                String categoryName = category.getName();
                //setmealDto设置分类名称
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        //将SetmealDto的信息封装到 新的分页信息中
        dtoPage.setRecords(dtoList);

        return Result.success(dtoPage);
    }

    //http://localhost:8080/setmeal/delete?ids=1560807554181844993,1560805469046554626

    @DeleteMapping("/delete")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info(" id = {}",ids);

        //数据库删除数据
        setmealService.deleteWithDish(ids);

        return Result.success("Success!");
    }

    //查询套餐 或者直接用实体接收就行
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#categoryId+'_'+#status")//注解进行缓存
    public Result<List<Setmeal>> list(@PathParam("categoryId") Long categoryId,@PathParam("status") int status){
//        String key = "setmeal_"+categoryId+"_"+status;
        List<Setmeal> setmealList = null;

//        //从Redis缓存中拿数据，
//        setmealList = (List<Setmeal>) redisTemplate.opsForValue().get(key);
//        //有就直接返回数据
//        if (setmealList!=null){
//            return Result.success(setmealList);
//        }

        //缓存中无数据从数据库中查找
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus,status);
        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        setmealList = setmealService.list(queryWrapper);

//        将数据放入redis
//        redisTemplate.opsForValue().set(key,setmealList);

        return Result.success(setmealList);
    }

    //请求网址: http://localhost:8080/setmeal/dish/1560805469046554626
    //请求方法: GET
    @GetMapping("/dish/{id}")
    public Result<Setmeal> getById(@PathVariable Long id){
        log.info("");
        Setmeal setmeal = setmealService.getById(id);
        SetmealDish setmealDish = setmealDishService.getById(id);
        return Result.success(null);
    }


    /*
    请求网址: http://localhost:8080/setmeal/status/0?ids=1563904001517412353
    请求方法: POST
     */
    /**
     * 停/启售套餐
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable int status,@RequestParam List<Long> ids){
        log.info("改变套餐状态~");
        log.info("status: {},{}",status,ids);
        //调用自定义方法：
        setmealService.updateStatus(status, ids);
        return Result.success("success");
    }

}
