package com.zmf.takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmf.takeaway.entity.Employee;
import com.zmf.takeaway.mapper.EmployeeMapper;
import com.zmf.takeaway.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 翟某人~
 * @version 1.0
 */

/**
 * 继承myBATis plus 提供的ServiceImpl
 */
@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
