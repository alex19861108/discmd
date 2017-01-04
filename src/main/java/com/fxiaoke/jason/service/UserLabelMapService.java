package com.fxiaoke.jason.service;

import com.alibaba.fastjson.JSON;
import com.fxiaoke.jason.entity.Label;
import com.fxiaoke.jason.entity.UserLabelMap;
import com.fxiaoke.jason.mapper.UserLabelMapMapper;
import com.github.cas.CasClient;
import com.github.cas.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by alex on 16/9/28.
 */
@Slf4j
@Service
public class UserLabelMapService {
  @Autowired
  private UserLabelMapMapper mapper;

  private CasClient casClient = CasClient.getInstance();

  public int insert(UserLabelMap userLabelMap) {
    return mapper.insert(userLabelMap);
  }

  public int insertAndSetObjectId(UserLabelMap userLabelMap) {
    return mapper.insertAndSetObjectId(userLabelMap);
  }

  public UserLabelMap findByUserAndLabel(Long userId, Long labelId) {
    return mapper.findByUserAndLabel(userId, labelId);
  }

  /**
   * 根据label,返回label下对应的用户
   * @param labelId
   * @return
   */
  public List<User> findUsersForLabel(Long labelId) {
    List<User> users = Lists.newLinkedList();
    List<UserLabelMap> userLabelMaps =  mapper.findByLabel(labelId);
    userLabelMaps.forEach(userLabelMap -> {
      String json = null;
      try {
        json = casClient.findUserById(userLabelMap.getUserId());
        User user = JSON.parseObject(json, User.class);
        users.add(user);
      } catch (IOException e) {
        log.error("cannot parse json: {}", json, e);
      }
    });

    return users;
  }

  /**
   * 根据label查找
   * @param labelId
   * @return
   */
  public List<UserLabelMap> findByLabel(Long labelId) {
    return  mapper.findByLabel(labelId);
  }

  /**
   * 根据用户id获取用户有权限的label
   * @param userId
   * @return
   */
  public List<Label> findLabelsForUser(Long userId) {
    return mapper.findLabelsForUser(userId);
  }

  /**
   * 根据user和lable删除
   * @param userId
   * @param labelId
   */
  public int deleteByUserAndLabel(Long userId, Long labelId) {
    return mapper.deleteByUserAndLabel(userId, labelId);
  }
}
