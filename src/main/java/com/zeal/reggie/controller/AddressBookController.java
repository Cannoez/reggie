package com.zeal.reggie.controller;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-27 17:16
 */
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.zeal.reggie.common.BaseContext;
import com.zeal.reggie.common.R;
import com.zeal.reggie.model.pojo.AddressBook;
import com.zeal.reggie.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 地址簿管理
 */
@Api(tags = "地址簿相关接口")
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    /**
     * 新增用户地址
     */
    @ApiOperation("新增用户地址")
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        // 通过上下文获取用户ID
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        // 构建更新条件
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);
        // 设置默认地址为1
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @ApiOperation("查询地址")
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该地址");
        }
    }

    /**
     * 查询默认地址
     */
    @ApiOperation("查询默认地址")
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @ApiOperation("根据条件查询地址")
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }

    /**
     * 根据地址id删除用户地址
     * @param id
     * @return
     */
    @ApiOperation("删除地址")
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id){
        if (id == null){
            return R.error("请求异常");
        }
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,id).eq(AddressBook::getUserId,BaseContext.getCurrentId());
        addressBookService.remove(queryWrapper);
        //addressBookService.removeById(id);  感觉直接使用这个removeById不太严谨.....
        return R.success("删除地址成功");
    }

    /**
     * 修改收货地址
     * @param addressBook
     * @return
     */
    @ApiOperation("修改地址")
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        if (addressBook == null){
            return R.error("请求异常");
        }
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }
}
