package com.zeal.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zeal.reggie.model.pojo.Category;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-23 23:52
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long  id);
}
