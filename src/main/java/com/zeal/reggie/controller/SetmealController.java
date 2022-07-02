package com.zeal.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zeal.reggie.common.R;
import com.zeal.reggie.dto.DishDto;
import com.zeal.reggie.dto.SetmealDto;
import com.zeal.reggie.model.pojo.*;
import com.zeal.reggie.service.CategoryService;
import com.zeal.reggie.service.SetmealDishService;
import com.zeal.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description: 套餐管理
 * @date: 2022-06-26 11:28
 */
@Slf4j
@RestController
@Api(tags = "套餐相关接口")
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @ApiOperation(value = "新增套餐接口")
    @CacheEvict(value = "setmealCache",allEntries = true)//清理setmealCache下的所有缓存数据
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增菜品成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @ApiOperation("套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true),
            @ApiImplicitParam(name = "name",value = "套餐名称",required = false)
    })
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage=new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper();
        //添加查询条件,根据name进行模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件 根据更新时间来降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @ApiOperation("套餐菜品和口味查询接口")
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改菜品
     * @param setmealDto
     * @return
     */
    @ApiOperation("套餐修改接口")
    @CacheEvict(value = "setmealCache",allEntries = true)//清理setmealCache下的所有缓存数据
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改菜品成功");
    }

    @ApiOperation("套餐状态接口")
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,Long[] ids){
        for (int i=0;i<ids.length;i++){
            //获取套餐
            Setmeal setmeal = setmealService.getById(ids[i]);
            //修改套餐状态
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("状态修改成功");

    }




    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @ApiOperation("套餐删除接口")
    @CacheEvict(value = "setmealCache",allEntries = true)//清理setmealCache下的所有缓存数据
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    @ApiOperation("套餐条件查询接口")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        //添加条件,查询状态为1(起售)的套餐
        queryWrapper.eq(Setmeal::getStatus,1);
        //排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);


        List<Setmeal> list = setmealService.list(queryWrapper);
//        List<SetmealDto> setmealDtoList=list.stream().map(item->{
//            SetmealDto setmealDto=new SetmealDto();
//            BeanUtils.copyProperties(item,setmealDto);
//            Long categoryId = item.getCategoryId();
//            Category category = categoryService.getById(categoryId);
//            if (category!=null){
//                String categoryName = category.getName();
//                setmealDto.setCategoryName(categoryName);
//            }
//            //当前菜品的id
//            Long setmealId = item.getId();
//            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealId);
//            //select * from setmeal Dish where setmeal_id=?
//            List<SetmealDish> setmealDishList = setmealDishService.list(lambdaQueryWrapper);
//            setmealDto.setSetmealDishes(setmealDishList);
//            return setmealDto;
//        }).collect(Collectors.toList());

        return R.success(list);
    }
}
