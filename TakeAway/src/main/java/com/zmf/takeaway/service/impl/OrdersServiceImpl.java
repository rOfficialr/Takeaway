package com.zmf.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmf.takeaway.common.BaseContext;
import com.zmf.takeaway.common.CustomException;
import com.zmf.takeaway.entity.*;
import com.zmf.takeaway.mapper.OrdersMapper;
import com.zmf.takeaway.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    //下单
    @Override
    @Transactional

    public void submit(Orders orders) {
        //用户ID
        Long id = BaseContext.getCurrentID();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,id);
        //查购物车
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (shoppingCartList ==null ||shoppingCartList.size() == 0){
            throw new CustomException("失败");
        }

        //查用户
        User user = userService.getById(id);
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        long number = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);//原子操作，保证多线程的情况下也不会出错，线程安全
        List<OrderDetail> orderDetils = shoppingCartList.stream().map(item -> {
            //订单明细
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(number);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get())); //总金额
        orders.setUserId(id);
        orders.setNumber(String.valueOf(number));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ?"":addressBook.getProvinceName())
        + (addressBook.getCityName() == null ?"":addressBook.getCityName())
        +(addressBook.getDistrictName() == null ?"":addressBook.getDistrictName())
        +(addressBook.getDetail() == null ?"":addressBook.getDetail()));

        this.save(orders);

        //订单明细表插入数据
        orderDetailService.saveBatch(orderDetils);

        //清空购物车
        shoppingCartService.remove(queryWrapper);

    }
}
