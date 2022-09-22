package com.zmf.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmf.takeaway.entity.Category;

/**
 * @author 翟某人~
 * @version 1.0
 */
public interface CategoryService extends IService<Category> {

    /**
     * 根据ID删除分类，
     * @param id
     */
    void deleteById(Long id);
}
