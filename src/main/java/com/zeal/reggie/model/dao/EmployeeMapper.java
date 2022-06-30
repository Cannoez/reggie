package com.zeal.reggie.model.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zeal.reggie.model.pojo.Employee;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeMapper  extends BaseMapper<Employee> {

}