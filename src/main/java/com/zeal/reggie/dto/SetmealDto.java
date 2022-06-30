package com.zeal.reggie.dto;

import com.zeal.reggie.model.pojo.Setmeal;
import com.zeal.reggie.model.pojo.SetmealDish;
import lombok.Data;

import java.util.List;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-26 11:54
 */
@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
