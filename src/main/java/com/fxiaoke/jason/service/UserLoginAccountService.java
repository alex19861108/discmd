package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.UserLoginAccount;
import com.fxiaoke.jason.mapper.UserLoginAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alex on 2016/11/8.
 */
@Service
public class UserLoginAccountService {
  @Autowired
  private UserLoginAccountMapper mapper;

  public List<UserLoginAccount> findByUser(long userId) {
    return mapper.findByUser(userId);
  }

  public List<UserLoginAccount> findAll() {
    return mapper.findAll();
  }

  public int update(UserLoginAccount userLoginAccount) {
    return mapper.update(userLoginAccount);
  }

  public int insert(UserLoginAccount userLoginAccount) {
    return mapper.insert(userLoginAccount);
  }

  public int delete(Long id) { return mapper.deleteById(id); }

  public UserLoginAccount findById(Long id) { return mapper.findById(id); }
}
