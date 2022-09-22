package com.zmf.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zmf.takeaway.common.BaseContext;
import com.zmf.takeaway.common.Result;
import com.zmf.takeaway.entity.AddressBook;
import com.zmf.takeaway.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.awt.geom.QuadCurve2D;
import java.util.List;

/**
 * @author 翟某人~
 * @version 1.0
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    //新增收货地址
    @PostMapping("/add")
    public Result<AddressBook> add(@RequestBody AddressBook addressBook){
        //从线程中得到用户ID；
        addressBook.setUserId(BaseContext.getCurrentID());
        addressBookService.save(addressBook);

        return Result.success(addressBook);
    }

    //设置默认地址
    @PutMapping("/default")
    public Result<AddressBook> setDefult(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentID());
        updateWrapper.set(AddressBook::getIsDefault,0);
        // update address_book set is_default=0 where user_id = ?
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        // update address_book set is_default=1 where user_id = ?
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }
    //得到默认地址
    @GetMapping("/default")
    public Result<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentID());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (null == addressBook){
            return Result.error("没有~");
        }else {
            return Result.success(addressBook);
        }
    }

    //查询用户地址
    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook){
        log.info("查用户地址~");
        addressBook.setUserId(BaseContext.getCurrentID());
        log.info(addressBook.getUserId()+" ……");

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null!= addressBook.getUserId(), AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return Result.success(list);
    }
    // 'url': `/addressBook/getById/${id}`,
    //    'method': 'get',
    @GetMapping("/getById/{id}")
    public Result<AddressBook> getById(@PathVariable Long id){
        log.info("按照ID查询地址，{}",id);

        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    //'url': '/addressBook/update',
    //        'method': 'put',
    @PutMapping("/update")
    public Result<String> update(@RequestBody AddressBook addressBook){
        log.info("更新地址，{}",addressBook);

        addressBookService.updateById(addressBook);

        return Result.success("Success~");
    }

    //'url': '/addressBook',
    //        'method': 'delete',
    @DeleteMapping("/delete")
    public Result<String> delete(@PathParam("id") Long ids){
        log.info("删除id= {}",ids);

        addressBookService.removeById(ids);
        return Result.success("删除成功！");
    }

}
