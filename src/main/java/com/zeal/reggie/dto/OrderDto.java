package com.zeal.reggie.dto;

import com.zeal.reggie.model.pojo.OrderDetail;
import com.zeal.reggie.model.pojo.Orders;
import lombok.Data;

import java.util.List;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-27 23:07
 */
@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;

    //用户名
    private String username;

    //手机号
    private String address;



}
