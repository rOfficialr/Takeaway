package com.zmf.takeaway.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmf.takeaway.entity.User;
import com.zmf.takeaway.mapper.UserMapper;
import com.zmf.takeaway.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
