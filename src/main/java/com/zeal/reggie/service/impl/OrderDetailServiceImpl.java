package com.zeal.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zeal.reggie.model.dao.OrderDetailMapper;
import com.zeal.reggie.model.pojo.OrderDetail;
import com.zeal.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-27 21:53
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
