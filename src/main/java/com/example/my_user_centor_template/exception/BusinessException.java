package com.example.my_user_centor_template.exception;

import com.example.my_user_centor_template.common.ErrorCode;

/**
 * 自定义异常类
 * :将原本的异常类扩充两个需要的字段
 *
 * @author yby
 */
public class BusinessException extends RuntimeException {

    private final int code;

    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);//message传给父类
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    //getter
    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
