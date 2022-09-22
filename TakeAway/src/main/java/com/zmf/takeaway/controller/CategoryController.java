package com.zmf.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmf.takeaway.common.Result;
import com.zmf.takeaway.entity.Category;
import com.zmf.takeaway.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 翟某人~
 * @version 1.0
 */

/**
 * 分类
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    public Result<String> add(@RequestBody Category category){
        log.info("新增： {}",category.toString());

        categoryService.save(category);
        return Result.success("新增成功~");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){
        log.info("分页查询：{},{}",page,pageSize);
        //分页构造器
        Page<Category> pageInfo = new Page(page,pageSize);
        //条件构造器，
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //根据sort排序
        LambdaQueryWrapper<Category> wrapper = queryWrapper.orderByAsc(Category::getSort);
        //分页查询
        Page<Category> categoryPage = categoryService.page(pageInfo, wrapper);

        return Result.success(categoryPage);
    }

    @DeleteMapping("/delete")
    public Result<String> deleteById(Long ids){
        log.info("删除分类： id:{}",ids);

        //调用自己的方法   要看是否关联dish
        categoryService.deleteById(ids);
//        categoryService.removeById(id);

        return Result.success("已删除！");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category.toString());

        categoryService.updateById(category);

        return Result.success("Success！");
    }

    @GetMapping("/list")
    public Result<List<Category>> list(Category category){

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //以传过来的Type为条件
        queryWrapper.eq(category.getType()!= null,Category::getType,category.getType());
        //按sort排序 和更新时间
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }


}
