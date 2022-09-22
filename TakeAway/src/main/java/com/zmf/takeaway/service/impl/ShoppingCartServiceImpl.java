package com.zmf.takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmf.takeaway.entity.ShoppingCart;
import com.zmf.takeaway.mapper.ShoppingCartMapper;
import com.zmf.takeaway.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
