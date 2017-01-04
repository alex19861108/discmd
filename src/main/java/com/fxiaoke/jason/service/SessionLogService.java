package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.SessionLog;
import com.fxiaoke.jason.mapper.SessionLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by alex on 2016/10/11.
 */
@Service
public class SessionLogService {
  @Autowired
  private SessionLogMapper mapper;

  public int insertAndSetObjectId(SessionLog sessionLog) {
    return mapper.insertAndSetObjectId(sessionLog);
  }

  public SessionLog findBySession(String sessionId) { return mapper.findBySession(sessionId); }

  public int update(SessionLog sessionLog) { return mapper.update(sessionLog); }
}
