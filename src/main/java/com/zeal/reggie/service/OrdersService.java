package com.zeal.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zeal.reggie.model.pojo.OrderDetail;
import com.zeal.reggie.model.pojo.Orders;

import java.util.List;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-27 21:51
 */
public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);

    /**
     * 根据订单id查询订单信息
     * @param id
     * @return
     */
    public List<OrderDetail> getOrderDetailByOrderId(Long id);
}
