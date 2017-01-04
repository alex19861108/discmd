package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.Label;
import com.fxiaoke.jason.entity.Machine;
import com.fxiaoke.jason.entity.MachineLabelMap;
import com.fxiaoke.jason.mapper.MachineLabelMapMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alex on 16/9/28.
 */
@Service
public class MachineLabelMapService {
  @Autowired
  private MachineLabelMapMapper mapper;

  public int insert(MachineLabelMap machineLabelMap) {
    return mapper.insert(machineLabelMap);
  }

  public int insertAndSetObjectId(MachineLabelMap machineLabelMap) {
    return mapper.insertAndSetObjectId(machineLabelMap);
  }

  public MachineLabelMap findByMachineAndLabel(Long machineId, Long labelId) {
    return mapper.findByMachineAndLabel(machineId, labelId);
  }

  /**
   * 根据label id, 获取所有的machine
   * @param labelId
   * @return
   */
  public List<Machine> findMachinesForLabel(Long labelId) {
    return mapper.findMachinesForLabel(labelId);
  }

  /**
   * 根据label查找machine
   * @param labelIds
   * @return
   */
  public List<Machine> findMachinesForLabels(String labelIds) {
    return mapper.findMachinesForLabels(labelIds);
  }

  /**
   * 如果不存在则创建MachineLabelMap
   * @param machineLabelMap
   * @return
   */
  public MachineLabelMap insertIfNotExists(MachineLabelMap machineLabelMap) {
    MachineLabelMap storedMachineLabelMap = findByMachineAndLabel(machineLabelMap.getMachineId(), machineLabelMap.getLabelId());
    if (storedMachineLabelMap == null) {
      insertAndSetObjectId(machineLabelMap);
    }

    return machineLabelMap;
  }

  /**
   * 根据machine id,获取所有的label
   * @param machineId
   * @return
   */
  public List<Label> findLabelsForMachine(Long machineId) {
    return mapper.findLabelsForMachine(machineId);
  }

  /**
   * 根据machine和user查找label
   * @param machineId
   * @param userId
   * @return
   */
  public List<Label> findUserOwnedLabelsForMachine(Long machineId, Long userId) {
    return mapper.findUserOwnedLabelsForMachine(machineId, userId);
  }

  /**
   * 根据machineId和labelId删除
   * @param machineId
   * @param labelId
   * @return
   */
  public int deleteByMachineAndLabel(Long machineId, Long labelId) {
    return mapper.deleteByMachineAndLabel(machineId, labelId);
  }
}
