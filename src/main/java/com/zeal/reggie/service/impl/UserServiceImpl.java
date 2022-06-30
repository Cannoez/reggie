package com.zeal.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zeal.reggie.model.dao.UserMapper;
import com.zeal.reggie.model.pojo.User;
import com.zeal.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-27 0:18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
