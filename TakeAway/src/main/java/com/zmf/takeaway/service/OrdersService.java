package com.zmf.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmf.takeaway.entity.Orders;

/**
 * @author 翟某人~
 * @version 1.0
 */

public interface OrdersService extends IService<Orders> {
    //下单
    void submit(Orders orders);
}
