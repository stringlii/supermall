package com.litianyi.search.exception;

import com.litianyi.common.constant.BizCodeEnum;
import com.litianyi.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author litianyi
 * @version 1.0
 * @description 集中处理所有异常
 * @date 2022/1/11 11:25 AM
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.litianyi.supermall.search.controller"})
public class SupermallExceptionControllerAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public R handleValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        result.getFieldErrors().forEach((item) -> errorMap.put(item.getField(), item.getDefaultMessage()));
        return R.error(BizCodeEnum.VALID_EXCEPTION).put("error", errorMap);
    }

    @ExceptionHandler(value = {Throwable.class})
    public R handleException(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return R.error(BizCodeEnum.PRODUCT_SEARCH_EXCEPTION).put("error", throwable);
    }
}
