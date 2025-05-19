package com.example.my_user_centor_template.mapper;

import com.example.my_user_centor_template.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yby2491525542
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2025-05-13 13:47:09
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




