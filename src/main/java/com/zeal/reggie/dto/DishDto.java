package com.zeal.reggie.dto;

import com.zeal.reggie.model.pojo.Dish;
import com.zeal.reggie.model.pojo.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-25 19:19
 */
@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors=new ArrayList<>();

    private String categoryName;

    private Integer copies;

}
