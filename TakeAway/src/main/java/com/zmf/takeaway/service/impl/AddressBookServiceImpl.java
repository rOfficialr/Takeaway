package com.zmf.takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmf.takeaway.entity.AddressBook;
import com.zmf.takeaway.mapper.AddressBookMapper;
import com.zmf.takeaway.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author 翟某人~
 * @version 1.0
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
