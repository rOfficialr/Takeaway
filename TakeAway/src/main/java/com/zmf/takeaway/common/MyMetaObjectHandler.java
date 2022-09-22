package com.zmf.takeaway.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author 翟某人~
 * @version 1.0
 */

/**
 * 自定义元对象数据处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    //从线程中得到登录者的ID
//    Long id = BaseContext.getCurrentID();

    @Override
    public void insertFill(MetaObject metaObject) {

//        log.info("id={}",id);
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());

        metaObject.setValue("createUser", BaseContext.getCurrentID());
        metaObject.setValue("updateUser", BaseContext.getCurrentID());

    }

    @Override
    public void updateFill(MetaObject metaObject) {

//        log.info("id={}",id);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentID());
    }
}
