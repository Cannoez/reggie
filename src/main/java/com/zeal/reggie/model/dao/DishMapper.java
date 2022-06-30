package com.zeal.reggie.model.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zeal.reggie.model.pojo.Dish;
import org.springframework.stereotype.Repository;

@Repository
public interface DishMapper extends BaseMapper<Dish> {
    int deleteByPrimaryKey(Long id);

    int insert(Dish record);

    int insertSelective(Dish record);

    Dish selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Dish record);

    int updateByPrimaryKey(Dish record);
}