package com.example.my_user_centor_template.exception;

import com.example.my_user_centor_template.common.BaseResponse;
import com.example.my_user_centor_template.common.ErrorCode;
import com.example.my_user_centor_template.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author yby
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获所有BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse BusinessExceptionHandle(BusinessException e) {
        log.error(" BusinessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());

    }

    /**
     * 捕获所有RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse RuntimeExceptionHandle(RuntimeException e) {
        log.error("RuntimeException: ", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }
}
