package com.zeal.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zeal.reggie.common.BaseContext;
import com.zeal.reggie.common.R;
import com.zeal.reggie.model.pojo.ShoppingCart;
import com.zeal.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-27 18:53
 */
@Slf4j
@Controller
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据{}",shoppingCart);
        //设置用户id 指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if (dishId!=null){
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else{
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        //select * from shopping_cart where user_id=? and dish_id/setmeal_id=?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        //如果已经存在,就在原来数量基础上加1
        if (cartServiceOne!=null){
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //如果不存在,则添加到购物车,数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne=shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list" )
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);

    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //delect from shopping_cart where user_id=?
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

    /**
     * 修改购物车
     * @return
     */
    @Transactional
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //delect from shopping_cart where user_id=? and dish_id/setmeal_id=?
        //条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        if (shoppingCart.getDishId()!=null){
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else if (shoppingCart.getSetmealId()!=null){
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        //查找到之后则再原来的基础上减一,如果数量为0则删除该订单
        if (cartServiceOne!=null){
            cartServiceOne.setNumber(cartServiceOne.getNumber()-1);
            Integer lastnumber = cartServiceOne.getNumber();
            if (lastnumber>0){
            shoppingCartService.updateById(cartServiceOne);
            cartServiceOne=shoppingCart;
            }else if (lastnumber==0){
                shoppingCartService.removeById(cartServiceOne.getId());
            }else {
                return R.error("购物车剩余数量不能小于0");
            }
        }else {
            return R.error("购物车异常");
        }
        return R.success(cartServiceOne);


    }
}
