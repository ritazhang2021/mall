package com.rita.modules.mall.product.exception;

import com.rita.common.exception.BizCodeEnume;
import com.rita.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Rita
 * @Date:6/30/2021 2:39 PM
 * 全局处理异常
 */
@Slf4j//属于lombok
//basePackages,指定处理范围

/*@ResponseBody//因为数据要按json返回
@ControllerAdvice(basePackages = "com/rita/modules/mall/product/controller")*/

@RestControllerAdvice(basePackages = "com.rita.modules.mall.product.controller")//注意不是/是.
public class MallExceptionControllerAdvice {
    //精确的异常放在前面

    //指定这个方法能处理哪些异常
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    //接收basePackages下类抛出来的异常
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题", e.getMessage(),"异常类型", e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError ->{
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());

        });
        //返回值如果是处理页面，可以是ModelAndView,这里我们还是用全局处理R
        //return R.error();
        //因为R实际上是个map，用ResponseBody,前端接收map会直接转成json
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(), BizCodeEnume.VAILD_EXCEPTION.getMsg() ).put("data",errorMap);
    }
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误",throwable);
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
