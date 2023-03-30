package com.test.exception;

import com.test.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author peng
 * @date 2023-03-29 18:36
 */
@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {
    /**
     * 异常拦截
     */
    @ExceptionHandler(value = Exception.class)
    public Result handlerException(Exception e) {
        log.error("", e);
        return Result.fail("400", e.getMessage());
    }
}
