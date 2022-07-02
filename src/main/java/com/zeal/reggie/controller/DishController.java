package com.zeal.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zeal.reggie.common.R;
import com.zeal.reggie.dto.DishDto;
import com.zeal.reggie.model.pojo.Category;
import com.zeal.reggie.model.pojo.Dish;
import com.zeal.reggie.model.pojo.DishFlavor;
import com.zeal.reggie.service.CategoryService;
import com.zeal.reggie.service.DishFlavorService;
import com.zeal.reggie.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:菜品管理
 * @date: 2022-06-25 18:48
 */
@Api(tags = "菜品相关接口")
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    DishService dishService;

    @Autowired
    CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @ApiOperation("新增菜品")
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        //清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //清理某个分类下面的菜品缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息的分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @ApiOperation("菜品分页")
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        //过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);}
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @ApiOperation("查看菜品及口味信息")
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @ApiOperation("修改菜品")
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        //清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //清理某个分类下面的菜品缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list( Dish dish){
//        //查询条件
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //添加条件,查询状态为1(起售)的菜品
//        queryWrapper.eq(Dish::getStatus,1);
//        //排序
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }

    @ApiOperation("菜品条件查询")
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;
        //动态构造key
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();//dish_1397844391040167938_1
        //先从redis中获取缓存数据
        dishDtoList= (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList!=null){
            //如果存在,直接返回,无需查询数据库
            return R.success(dishDtoList);
        }
        //查询条件
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件,查询状态为1(起售)的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);


        List<Dish> list = dishService.list(queryWrapper);
        dishDtoList=list.stream().map(item->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //select * from dish flavor where dish_id=?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        //如果不存在,需要查询数据库,将查到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

    /**
     * 菜品的起售和停售
     */
    @ApiOperation("菜品状态")
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,Long[] ids){
//        log.info("setmeal====>{},status==>{}" + ids +"====>" + status);
        for (int i=0;i<ids.length;i++) {
            //获取菜品
            Dish dish = dishService.getById(ids[i]);
            dish.setStatus(status);
            //修改菜品状态
            dishService.updateById(dish);
        }
        return R.success("修改菜品信息成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @ApiOperation("删除菜品")
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);

        //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);


        return R.success("删除成功");
    }




}
