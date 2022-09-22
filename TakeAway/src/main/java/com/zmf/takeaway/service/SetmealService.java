package com.zmf.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmf.takeaway.dto.SetmealDto;
import com.zmf.takeaway.entity.Setmeal;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 翟某人~
 * @version 1.0
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 保存套餐和套餐对应的dish的关联关系
     * @param setmealDto
     */
    void addWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐和套餐对应的dish的关联关系
     * @param ids
     */
    void deleteWithDish(List<Long> ids);

    /**
     * 改变状态
     * @param status
     * @param ids
     */
    void updateStatus(int status,List<Long> ids);
}
