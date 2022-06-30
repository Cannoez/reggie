package com.zeal.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zeal.reggie.model.dao.EmployeeMapper;
import com.zeal.reggie.model.pojo.Employee;
import com.zeal.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-22 22:59
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
}
