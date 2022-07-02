package com.zeal.reggie.dto;

import com.zeal.reggie.model.pojo.Setmeal;
import com.zeal.reggie.model.pojo.SetmealDish;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-26 11:54
 */
@ApiModel("套餐DTO")
@Data
public class SetmealDto extends Setmeal {
    @ApiModelProperty("套餐菜品")
    private List<SetmealDish> setmealDishes;
    @ApiModelProperty("套餐分类")
    private String categoryName;
}
