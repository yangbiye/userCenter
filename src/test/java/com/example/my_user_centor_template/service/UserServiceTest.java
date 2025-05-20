package com.example.my_user_centor_template.service;

import com.example.my_user_centor_template.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


/**
 * 用户服务测试
 *
 * @author yby
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("test1");
        user.setUserAccount("123");
        user.setAvatarUrl("xx");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("123");
        boolean result =  userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "yby_test_2";
        String password = "";
        String checkPassword = "123456";
        String userCode = "1";
        //空密码
        long testResult = userService.userRegister(userAccount, password, checkPassword,userCode);
        Assertions.assertEquals(-1, testResult);

        //账号小于四位
        userAccount = "y";
        testResult = userService.userRegister(userAccount, password, checkPassword,userCode);
        Assertions.assertEquals(-1, testResult);

        //密码小于八位
        userAccount = "yby_test_2";
        password = "123456";
        testResult = userService.userRegister(userAccount, password, checkPassword,userCode);
        Assertions.assertEquals(-1, testResult);

        //账号特殊字符
        userAccount = "！ybyaa";
        password = "12345678";
        checkPassword = "12345678";
        testResult = userService.userRegister(userAccount, password, checkPassword,userCode);
        Assertions.assertEquals(-1, testResult);

        userAccount = "y@byaa";
        testResult = userService.userRegister(userAccount, password, checkPassword,userCode);
        Assertions.assertEquals(-1, testResult);

        userAccount = "yb--yaa";
        testResult = userService.userRegister(userAccount, password, checkPassword,userCode);
        Assertions.assertEquals(-1, testResult);

        //确认密码不等
        checkPassword = "123456789";
        testResult = userService.userRegister(userAccount, password, checkPassword,userCode);
        Assertions.assertEquals(-1, testResult);

        //账户重复
        userAccount = "yby_test_1";
        password = "123456789";
        testResult = userService.userRegister(userAccount, password, checkPassword,userCode);
        Assertions.assertEquals(-1, testResult);

        //成功测试
        userAccount = "yby_test_3";
        testResult = userService.userRegister(userAccount, password, checkPassword,userCode);
        Assertions.assertTrue(testResult > 0);
    }
}