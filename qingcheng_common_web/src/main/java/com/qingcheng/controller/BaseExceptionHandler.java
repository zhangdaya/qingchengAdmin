package com.qingcheng.controller;

import com.qingcheng.entity.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理类（全局异常类）
 * @ ControllerAdvice（统一异常处理）需要配合@ExceptionHandler使用。
 * 当将异常抛到controller层时,可以对异常进行统一处理,规定返回的json格式或是跳转到一个错误页面
 */
@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        System.out.println("调用了公共异常处理类");
        return new Result(1,e.getMessage());
    }


}
