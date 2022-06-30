package com.zeal.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zeal.reggie.exception.CustomException;
import com.zeal.reggie.model.dao.CategoryMapper;
import com.zeal.reggie.model.pojo.Category;
import com.zeal.reggie.model.pojo.Dish;
import com.zeal.reggie.model.pojo.Setmeal;
import com.zeal.reggie.service.CategoryService;
import com.zeal.reggie.service.DishService;
import com.zeal.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-23 23:53
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealServices;


    /**
     * 根据id删除分类,删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件,根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品,如果已经关联,抛出一个业务异常
        if (count1>0){
            //已经关联菜品,抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }
        //查询当前分类是否关联了套餐,如果已经关联,抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealServices.count(setmealLambdaQueryWrapper);
        if (count2>0){
            //已经关联套餐,抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }

        //正常删除
        super.removeById(id);
    }
}
