package com.example.my_user_centor_template.controller;

import com.example.my_user_centor_template.constant.UserConstant;
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
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest == null){
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String CheckPassword = userRegisterRequest.getCheckPassword();
        String userCode = userRegisterRequest.getUserCode();
        //controller对请求参数本身校验(与service层中的校验不冲突，service是业务逻辑校验，会被其他类调用，而此处仅仅是请求校验)
        if(StringUtils.isAnyBlank(userAccount,userPassword,CheckPassword,userCode)){
            return null;
        }
        return userService.userRegister(userAccount, userPassword, CheckPassword,userCode);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if(userLoginRequest == null){
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        return userService.doLogin(userAccount, userPassword,request);
    }

    @PostMapping("/logout")
    public Integer userLogout(HttpServletRequest request) {
        userService.userLogout(request);
        return 1;
    }

    @GetMapping("/search")
    public List<User> searchUser(String userName, HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        System.out.println(user.getUsername());
        //仅管理员可查询
        if(!isAdmin(request)){
            return new ArrayList<>();
        }
        return userService.userSearch(userName);
    }

    @PostMapping("/delete")
    public boolean userDelete(@RequestBody long id, HttpServletRequest request) {
        if(id < 0){
            return false;
        }
        //仅管理员可删
        if(!isAdmin(request)){
            return false;
        }
        return userService.userDelete(id);
    }


    // 鉴权
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
