package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.SessionMachineStatus;
import com.fxiaoke.jason.mapper.SessionMachineStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alex on 2016/11/22.
 */
@Service
public class SessionMachineStatusService {
  @Autowired
  private SessionMachineStatusMapper mapper;

  public int insert(SessionMachineStatus sessionMachineStatus) {
    return mapper.insert(sessionMachineStatus);
  }

  public SessionMachineStatus findBySessionAndMachine(Long sessionId, Long machineId) {
    return mapper.findBySessionAndMachine(sessionId, machineId);
  }

  public int insertAndSetObjectId(SessionMachineStatus sessionMachineStatus) {
    return mapper.insertAndSetObjectId(sessionMachineStatus);
  }

  public int update(SessionMachineStatus sessionMachineStatus) {
    return mapper.update(sessionMachineStatus);
  }

  public List<SessionMachineStatus> findBySession(String sessionId) {
    return mapper.findBySession(sessionId);
  }
}
