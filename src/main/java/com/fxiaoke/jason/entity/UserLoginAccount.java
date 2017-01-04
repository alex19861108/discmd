package com.fxiaoke.jason.entity;

import com.github.mybatis.entity.IdEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by alex on 2016/11/8.
 */
@Table(name = "tbl_user_login_account")
@Getter
@Setter
@NoArgsConstructor
public class UserLoginAccount extends IdEntity {
  private long userId;
  private String account;
  private String password;
  @Transient
  private String username;
  @Transient
  private String realname;
}
