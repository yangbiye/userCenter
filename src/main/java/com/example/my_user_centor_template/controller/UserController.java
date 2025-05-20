package com.example.my_user_centor_template.controller;

import com.example.my_user_centor_template.common.BaseResponse;
import com.example.my_user_centor_template.common.ErrorCode;
import com.example.my_user_centor_template.common.ResultUtils;
import com.example.my_user_centor_template.constant.UserConstant;
import com.example.my_user_centor_template.exception.BusinessException;
import com.example.my_user_centor_template.model.domain.User;
import com.example.my_user_centor_template.model.request.UserLoginRequest;
import com.example.my_user_centor_template.model.request.UserRegisterRequest;
import com.example.my_user_centor_template.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.example.my_user_centor_template.constant.UserConstant.ADMIN_ROLE;
import static com.example.my_user_centor_template.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author yby
 */
@RestController //适用于编写restful风格的api,返回值默认为json格式
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String CheckPassword = userRegisterRequest.getCheckPassword();
        String userCode = userRegisterRequest.getUserCode();
        //controller对请求参数本身校验(与service层中的校验不冲突，service是业务逻辑校验，会被其他类调用，而此处仅仅是请求校验)
        if(StringUtils.isAnyBlank(userAccount,userPassword,CheckPassword,userCode)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, CheckPassword,userCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = userService.doLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);

    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = userService.getCurrentUser(request);
        if(currentUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(currentUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String userName, HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        //仅管理员可查询
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"非管理员，不可执行此操作");
        }
        List<User> users = userService.userSearch(userName);
        return ResultUtils.success(users);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(@RequestBody long id, HttpServletRequest request) {
        if(id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //仅管理员可删
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"非管理员，不可执行此操作");
        }
        boolean result =  userService.userDelete(id);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除用户失败");
        }
        return ResultUtils.success(result);
    }


    // 鉴权
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
