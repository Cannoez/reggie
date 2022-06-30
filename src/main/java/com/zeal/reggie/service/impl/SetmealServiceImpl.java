package com.zeal.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zeal.reggie.dto.DishDto;
import com.zeal.reggie.dto.SetmealDto;
import com.zeal.reggie.exception.CustomException;
import com.zeal.reggie.model.dao.SetmealMapper;
import com.zeal.reggie.model.pojo.Setmeal;
import com.zeal.reggie.model.pojo.SetmealDish;
import com.zeal.reggie.service.SetmealDishService;
import com.zeal.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-24 22:58
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐,同时保存套餐和菜品的管理关系
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal,执行insert操作
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息,操作setmeal_dish 执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }
    /**
     * 根据id查询套餐信息和对应的菜品信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //查询套餐基本信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        //查询套餐对应的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新setmeal表
        this.updateById(setmealDto);
        //清理当前套餐对应的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //添加提交过来的口味数据 setmeal_Dish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐,同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态,确实是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrappe=new LambdaQueryWrapper<>();
        queryWrappe.in(Setmeal::getId,ids);
        queryWrappe.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrappe);
        if (count>0){
            //如果不能删除,抛出一个业务的异常
            throw new CustomException("套餐正在售卖中,不能删除");
        }
        //如果可以删除,先删除套餐表中的数据
        this.removeByIds(ids);

        //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
