package com.zeal.reggie.model.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zeal.reggie.model.pojo.DishFlavor;
import org.springframework.stereotype.Repository;

@Repository
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
    int deleteByPrimaryKey(Long id);

    int insert(DishFlavor record);

    int insertSelective(DishFlavor record);

    DishFlavor selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DishFlavor record);

    int updateByPrimaryKey(DishFlavor record);
}