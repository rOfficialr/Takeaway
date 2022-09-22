package com.zmf.takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmf.takeaway.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 翟某人~
 * @version 1.0
 */

/**
 * 继承BaseMapper并加上泛型Employee，就直接与Employee实体类进行绑定；
 * 同时，继承了BaseMapper之后也把常见的增删改查方法继承过来了
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
