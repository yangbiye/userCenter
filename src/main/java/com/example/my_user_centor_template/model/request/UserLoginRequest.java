package com.example.my_user_centor_template.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求类
 *
 * @author yby
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;
}
