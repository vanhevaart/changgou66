package org.changgou.framework.exception;

import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author:  HZ
 * <p>公共的异常处理类
 * Create:  2019/8/10  19:36
 */
@ControllerAdvice  // 代理此类来捕获异常
public class BaseExceptionHandler {

    /**
     * 异常处理
     * @param e 捕获到的异常
     * @return 向前端返回的合适结果
     */
    @ExceptionHandler(Exception.class) // 代表此方法来处理异常
    @ResponseBody
    public Result exception(Exception e){
        e.printStackTrace();
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }
}
