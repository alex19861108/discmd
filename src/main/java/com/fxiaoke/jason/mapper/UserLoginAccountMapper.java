package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.UserLoginAccount;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by alex on 2016/11/8.
 */
public interface UserLoginAccountMapper extends ICrudMapper<UserLoginAccount> {

  @Select("SELECT * FROM tbl_user_login_account WHERE user_id=#{userId}")
  List<UserLoginAccount> findByUser(@Param("userId") Long userId);
}
