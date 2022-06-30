package com.zeal.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zeal.reggie.model.dao.ShoppingCartMapper;
import com.zeal.reggie.model.pojo.ShoppingCart;
import com.zeal.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-27 18:52
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
