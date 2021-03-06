package com.zeal.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zeal.reggie.common.R;
import com.zeal.reggie.model.pojo.Employee;
import com.zeal.reggie.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-22 23:02
 */
@Api(tags = "后台相关接口")
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 后台管理系统登录
     * @param session
     * @param employee
     * @return
     */
    @ApiOperation("后台登录")
    @PostMapping("/login")
    public R<Employee> login(HttpSession session, @RequestBody Employee employee){
        //1 将页面提交的密码进行MD5加密
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        //2 根据用户名查数据库
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3 如果没有查询到则返回登录失败结果
        if (emp==null){
            return R.error("登录失败");
        }
        //4 密码比对,如果不一致则返回登录失败
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //5 查看员工状态,如果为已禁用状态,则返回员工已禁用结果
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        //6 登录成功,将员工id存入Session
        session.setAttribute("employee",emp.getId());
        emp.setPassword(null);
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param session
     * @return
     */
    @ApiOperation("后台退出")
    @PostMapping("/logout")
    public R<String> logout(HttpSession session){
        session.invalidate();
        //session.removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @ApiOperation("新增员工")
    @PostMapping
    public R<String> save(HttpSession session,@RequestBody Employee employee){
        log.info("新增员工,员工信息:{}",employee.toString());
        //设置初始密码123456,需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
        //Long empId = (Long)session.getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @ApiOperation("员工分页查询")
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @ApiOperation("修改员工id")
    @PutMapping
    public  R<String> update(HttpSession session, @RequestBody Employee employee){
        log.info(employee.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser((Long) session.getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @ApiOperation("根据id查询")
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
