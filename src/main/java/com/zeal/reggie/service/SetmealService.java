package com.zeal.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zeal.reggie.dto.DishDto;
import com.zeal.reggie.dto.SetmealDto;
import com.zeal.reggie.model.pojo.Setmeal;

import java.util.List;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-24 22:58
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐,同时保存套餐和菜品的管理关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 根据id查询套餐信息和对应的菜品信息
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id);

    /**
     * 修改套餐
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);


    /**
     * 删除套餐,同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);


}
