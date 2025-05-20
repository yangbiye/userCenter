package com.example.my_user_centor_template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.my_user_centor_template.constant.UserConstant;
import com.example.my_user_centor_template.model.domain.User;
import com.example.my_user_centor_template.service.UserService;
import com.example.my_user_centor_template.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.my_user_centor_template.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author yby
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 盐值：混淆密码
     */
    private static final String SALT = "yby";

    /**
     * 正则表达式：验证账户合理性
     */
    private static final String pattern = "^[a-zA-Z0-9_]+$";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String userCode) {
        //1.校验

        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,userCode)){
            return -1;
        }
        if(userAccount.length() < 4 || userPassword.length() < 8 || !userPassword.equals(checkPassword)||userCode.length() > 5){
            return -1;
        }
        //账号不能包含特殊字符(只允许字母、数字、下划线)
        if(!userAccount.matches(pattern)){
            return -1;
        }

        //账号不能重复（放在最后，减少查询次数，提高效率）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);//等价于 “select from user where userAccount = userAccount”
        long count = this.count(queryWrapper);//this表示自己，亦可以注入userMapper,使用其中的方法，但比起自身会少一部分功能
        if(count > 0){
            return -1;
        }
        //用户编码不可重复（注册次数较少，所以不复用代码减少数据库访问）
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userCode", userCode);
        count = this.count(queryWrapper);
        if(count > 0){
            return -1;
        }

        //2.密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());

        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserCode(userCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            return -1;
        }
        //id设置了自增，有回填机制
        return user.getId();
    }

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        if(userAccount.length() < 4 || userPassword.length() < 8 ){
            return null;
        }
        // 账号不能包含特殊字符(只允许字母、数字、下划线)
        if(!userAccount.matches(pattern)){
            return null;
        }
        // 2.密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.getOne(queryWrapper);
        if(user == null) {
            log.info("login fail,userAccount cannot match userPassword");
            return null;
        }
        //3.用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    @Override
    public List<User> userSearch(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(userName)){
            queryWrapper.like("userName", userName);
        }
        List<User> users = this.list(queryWrapper);
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public boolean userDelete(long id) {
        if(id <= 0){
            return false;
        }
        return this.removeById(id);
    }

    /**
     * 用户注销
     * @param request http请求:用于获取当前用户
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 用户信息脱敏
     */
    public User getSafetyUser(User originUser) {
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(0);
        safetyUser.setUserCode(originUser.getUserCode());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }
}




