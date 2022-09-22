package com.zmf.takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmf.takeaway.entity.OrderDetail;
import com.zmf.takeaway.mapper.OrderDetailMapper;
import com.zmf.takeaway.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
