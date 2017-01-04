package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.Label;
import com.fxiaoke.jason.entity.Machine;
import com.fxiaoke.jason.entity.MachineLabelUnion;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by alex on 2016/11/1.
 */
@Service
public class MachineLabelUnionService {
  @Autowired
  private MachineService machineService;

  @Autowired
  private MachineLabelMapService machineLabelMapService;

  @Autowired
  private UserLabelMapService userLabelMapService;

  // 缓存名称
  private static final String MACHINE_LABEL_UNIONS = "MachineLabelUnions";
  private static final String MACHINES_FOR_USER = "MachinesForUser";

  /**
   * 返回附属label的机器列表
   * @return
   */
  @Cacheable(value = MACHINE_LABEL_UNIONS)
  public List<MachineLabelUnion> findAll() {
    List<Machine> machines = machineService.findAll();

    return patchLabelForMachines(machines);
  }

  /**
   * 清除缓存
   * @param userId
   */
  public void clearMachineLabelUnionsCacheForUser(Long userId) {
    if (SecurityUtils.getSubject().hasRole("admin")) {
      clearCache();
    } else {
      clearCacheForUser(userId);
    }
  }

  /**
   * 清除缓存
   */
  @CacheEvict(value = MACHINE_LABEL_UNIONS)
  public void clearCache() {}

  @CacheEvict(value = MACHINES_FOR_USER, key = "#userId")
  public void clearCacheForUser(Long userId) {}

  /**
   * 根据用户名找出用户有权限的机器
   * @param userId
   * @return
   */
  @Cacheable(value = MACHINES_FOR_USER, key = "#userId")
  public List<MachineLabelUnion> findForUser(Long userId) {
    // 用户有权限的label
    List<Label> labels = userLabelMapService.findLabelsForUser(userId);

    // label下的所有机器
    List<Long> labelIds = Lists.newArrayList();
    labels.forEach(label -> labelIds.add(label.getId()));
    String labelIdStrs = Joiner.on(',').join(labelIds);
    List<Machine> machines = Lists.newLinkedList();
    if (!Strings.isNullOrEmpty(labelIdStrs)) {
      machines = machineLabelMapService.findMachinesForLabels(labelIdStrs);
    }

    // return patchLabelForMachines(machines);
    return patchUserOwnedLabelForMachines(machines, userId);
  }

  /**
   * 批量为机器添加label信息
   * @param machines
   * @return
   */
  public List<MachineLabelUnion> patchLabelForMachines(List<Machine> machines) {
    List<MachineLabelUnion> machineLabelUnions = Lists.newLinkedList();

    for (Machine machine : machines) {

      MachineLabelUnion machineLabelUnion = patchLabelForMachine(machine);

      machineLabelUnions.add(machineLabelUnion);
    }
    return machineLabelUnions;
  }

  /**
   * 批量为机器添加用户有权限的label信息
   * @param machines
   * @param userId
   * @return
   */
  public List<MachineLabelUnion> patchUserOwnedLabelForMachines(List<Machine> machines, Long userId) {
    List<MachineLabelUnion> machineLabelUnions = Lists.newLinkedList();
    for (Machine machine : machines) {
      MachineLabelUnion machineLabelUnion = patchUserOwnedLabelForMachine(machine, userId);

      machineLabelUnions.add(machineLabelUnion);
    }
    return machineLabelUnions;
  }

  /**
   * 为机器patch label信息
   * @param machine
   * @return
   */
  public MachineLabelUnion patchLabelForMachine(Machine machine) {
    List<Label> labels = machineLabelMapService.findLabelsForMachine(machine.getId());
    MachineLabelUnion machineLabelUnion = new MachineLabelUnion();
    machineLabelUnion.setMachine(machine);
    machineLabelUnion.setLabels(labels);

    return machineLabelUnion;
  }

  public MachineLabelUnion patchUserOwnedLabelForMachine(Machine machine, Long userId) {
    List<Label> labels = machineLabelMapService.findUserOwnedLabelsForMachine(machine.getId(), userId);
    MachineLabelUnion machineLabelUnion = new MachineLabelUnion();
    machineLabelUnion.setMachine(machine);
    machineLabelUnion.setLabels(labels);

    return machineLabelUnion;
  }

  /**
   * 内聚动作: 根据用户Id查找用户有权限的机器信息
   * @param userId
   * @return
   */
  public List<MachineLabelUnion> findMachinesForUser(Long userId) {
    List<MachineLabelUnion> machineLabelUnions;
    if (SecurityUtils.getSubject().hasRole("admin")) {
      machineLabelUnions = findAll();
    } else {
      machineLabelUnions = findForUser(userId);
    }
    return machineLabelUnions;
  }
}
