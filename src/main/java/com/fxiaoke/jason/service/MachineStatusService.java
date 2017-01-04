package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.MachineStatus;
import com.fxiaoke.jason.mapper.MachineStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by alex on 2016/10/10.
 */
@Service
public class MachineStatusService {
  @Autowired
  private MachineStatusMapper mapper;

  /**
   * 根据machineId和userId查找
   * @param machineId
   * @param userId
   * @return
   */
  private MachineStatus findByMachineIdAndUserId(Long machineId, long userId) {
    return mapper.findByMachineIdAndUserId(machineId, userId);
  }

  public int update(MachineStatus machineStatus) {
    return mapper.update(machineStatus);
  }
}
