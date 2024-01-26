package com.dhyjlas.mys.config;

import com.dhyjlas.mys.exception.BusinessException;
import com.dhyjlas.mys.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>File: ExceptionControllerAdvice.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(BusinessException.class)
    public JsonResult businessExceptionHandler(BusinessException e) {
        log.error("", e);
        return JsonResult.failure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public JsonResult exceptionHandler(Exception e) {
        log.error("", e);
        return JsonResult.failure();
    }
}
