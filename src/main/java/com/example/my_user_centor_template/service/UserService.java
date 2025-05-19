package com.example.my_user_centor_template.service;

import com.example.my_user_centor_template.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author yby
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 用户信息（脱敏）
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return 用户列表
     */
    List<User> userSearch(String userName);

    /**
     * 根据id删除用户
     *
     * @param id 用户id
     * @return bool
     */
    boolean userDelete(long id);
}
