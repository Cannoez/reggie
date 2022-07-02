package com.zeal.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zeal.reggie.common.BaseContext;
import com.zeal.reggie.common.R;
import com.zeal.reggie.dto.DishDto;
import com.zeal.reggie.dto.OrderDto;
import com.zeal.reggie.model.pojo.*;
import com.zeal.reggie.service.OrdersService;
import com.zeal.reggie.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:订单
 * @date: 2022-06-27 21:55
 */
@Api(tags = "订单相关接口")
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrdersService ordersService;
    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @ApiOperation("用户下单")
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 后台分页显示
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation("后台分页")
    @GetMapping("/page")
    public  R<Page> page(int page, int pageSize, String number,
                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime ){
        log.info("orderTime{}",beginTime);
        log.info("checkoutTime{}",endTime);
        //分页构造器
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper();
        //过滤条件
        queryWrapper.like(number!=null,Orders::getNumber,number);
        queryWrapper.ge(beginTime!=null,Orders::getOrderTime,beginTime);
        queryWrapper.le(endTime!=null,Orders::getCheckoutTime,endTime);
        //排序条件
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        //执行分页查询
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改订单状态
     * @return
     */
    @ApiOperation("订单状态")
    @PutMapping
    public R<String> status(@RequestBody Orders orders){
         ordersService.updateById(orders);
        return R.success("订单状态修改成功");
    }

    /**
     * 前端分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation("前端分页")
    @GetMapping("/userPage")
    public R<Page> page(Integer page, Integer pageSize){
        //分页构造器
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Page<OrderDto> orderDtoPage=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //添加排序条件,根据sort进行排序
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        //进行分页查询
        ordersService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,orderDtoPage,"records");
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> list=records.stream().map(item->{
            OrderDto orderDto=new OrderDto();
            BeanUtils.copyProperties(item,orderDto);
            Long OrderId = item.getId();//获取订单id
            List<OrderDetail> orderDetailList = ordersService.getOrderDetailByOrderId(OrderId);
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());
        orderDtoPage.setRecords(list);

        return R.success(orderDtoPage);
    }

    /**
     * 再来一单功能
     * 查询订单信息
     * 将之前购物车的东西清除
     * 将订单信息返回给购物车
     *
     * @param orders
     * @return
     */
    @ApiOperation("再来一单")
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        //根据订单id找订单详细
        List<OrderDetail> orderDetailList = ordersService.getOrderDetailByOrderId(orders.getId());
        //根据订单详细添加商品到购物车
        List<ShoppingCart> list=orderDetailList.stream().map(item->{
            ShoppingCart shoppingCart=new ShoppingCart();
            BeanUtils.copyProperties(item,shoppingCart,"userId");
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(list);
        return R.success("再来一单成功");
    }

}
