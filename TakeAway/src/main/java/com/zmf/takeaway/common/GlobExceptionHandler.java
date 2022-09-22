package com.zmf.takeaway.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author 翟某人~
 * @version 1.0
 */

/**
 * 全局异常处理，对于@RestController 和@Controller的类的异常进行处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobExceptionHandler {

    //这个注解很重要异常处理的
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        log.info(e.getMessage());
        if (e.getMessage().contains("Duplicate entry")){//Duplicate entry双重输入
            String[] strings = e.getMessage().split(" ");
            return Result.error(strings[2]+"已存在");
        }
        return Result.error("错误~");
    }

    //这个注解很重要异常处理的
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException e){
        log.info(e.getMessage());

        return Result.error(e.getMessage());
    }
}
