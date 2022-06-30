package com.zeal.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zeal.reggie.dto.DishDto;
import com.zeal.reggie.model.pojo.Dish;

import java.util.List;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-24 22:56
 */
public interface DishService extends IService<Dish> {

    //新增菜品,同时插入菜品对应的口味数据,需要操作两张表 dish dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息,同时更新对应的口味信息
    public void updateWithFlavor(DishDto dishDto);

    public void  deleteWithFlavor(List<Long> id);
}
