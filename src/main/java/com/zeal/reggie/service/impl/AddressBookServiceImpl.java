package com.zeal.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zeal.reggie.model.dao.AddressBookMapper;
import com.zeal.reggie.model.pojo.AddressBook;
import com.zeal.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-27 17:15
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
