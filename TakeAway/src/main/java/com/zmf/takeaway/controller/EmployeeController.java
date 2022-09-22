package com.zmf.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmf.takeaway.common.Result;
import com.zmf.takeaway.entity.Employee;
import com.zmf.takeaway.service.EmployeeService;
import com.zmf.takeaway.service.impl.EmployeeServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Slf4j
//@Controller
//@ResponseBody
@RestController
@RequestMapping("/employee")
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     *前端返回的是用户名和密码，这里使用employee对象接收；
     */
    @PostMapping("/login")
    public Result<Employee> logion(HttpServletRequest request,@RequestBody Employee employee){
        //将接收的密码先通过md5加密
        String password = employee.getPassword();
        String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据用户名查数据库
            LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Employee::getUsername,employee.getUsername());
            //数据库中username 设置了unique 唯一
            Employee emp = employeeService.getOne(queryWrapper);
        //比较密码
        if (emp == null){
            return Result.error("无该用户名，请先注册！");
        }
        if (!emp.getPassword().equals(pwd)){
            return Result.error("密码错误！");
        }
        //看员工是否被禁用
        if (emp.getStatus() == 0){
            return Result.error("被禁用！");
        }

        //成功的话将ID存入Session
        request.getSession().setAttribute("employeeId",emp.getId());

        return Result.success(emp);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
//        清除Session中信息
        request.getSession().removeAttribute("employeeId");
        return Result.success("已经退出！");
    }

    /**
     * 添加员工
     */
    @PostMapping("/add")
    public Result<String> add(HttpServletRequest request,@RequestBody Employee employee){
//        id=null, username='123', name='123', password='null',
//        phone='13020884756', sex='1', idNumber='123654789654123654'
        log.info("新增的员工：{}",employee.toString());
        //创建时间 当前时间
        employee.setCreateTime(LocalDateTime.now());
        //创建人的ID
        employee.setCreateUser((Long)request.getSession().getAttribute("employeeId"));
        //更新人的ID
        employee.setUpdateUser((Long)request.getSession().getAttribute("employeeId"));
        //更新时间
        employee.setUpdateTime(LocalDateTime.now());

        //这个save是继承的接口提供的
        boolean save = employeeService.save(employee);
//        if (!save){
//            return Result.error("新增员工出错！");
//        }
        return Result.success("成功");
    }
//    http://localhost:8080/employee/page?page=1&pageSize=10&name=1
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        log.info("page:{};pageSize:{};name:{}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        if ( name!= null){
            //添加过滤条件
            queryWrapper.like(Employee::getName,name);
        }
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //查询
        Page pageEnd = employeeService.page(pageInfo, queryWrapper);

        return Result.success(pageEnd);
    }

    /**
     * 根据ID来修改信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping("/update")
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee){

        Long employeeId = (Long) request.getSession().getAttribute("employeeId");
        log.info("修改： {}",employee.toString());

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(employeeId);

        employeeService.updateById(employee);

        return Result.success("修改成功！");
    }

    /**
     * 根据ID查询员工信息（回显）
     * @param id
     * @return
     */
    @RequestMapping("/selectById/{id}")
    public Result<Employee> getById(@PathVariable Long id){

        log.info("ID查员工");
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

}
