package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.Label;
import com.fxiaoke.jason.entity.LabelMachineUnion;
import com.fxiaoke.jason.entity.Machine;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alex on 2016/11/10.
 */
@Service
public class LabelMachineService {

  @Autowired
  private LabelService labelService;

  @Autowired
  private MachineLabelMapService machineLabelMapService;
  @Autowired
  private UserLabelMapService userLabelMapService;

  // 缓存名称
  private static final String LABEL_MACHINE_UNIONS = "LabelMachineUnions";
  private static final String LABELS_FOR_USER = "LabelsForUser";

  /**
   * 查找所有
   * @return
   */
  @Cacheable(value = LABEL_MACHINE_UNIONS)
  public List<LabelMachineUnion> findAll() {
    List<Label> labels = labelService.findAll();

    return patchMachineForLabels(labels);
  }

  /**
   * 清空cache
   * @param userId
   */
  public void clearLabelCacheForUser(Long userId) {
    if (SecurityUtils.getSubject().hasRole("admin")) {
      clearCache();
    } else {
      clearCacheForUser(userId);
    }
  }

  /**
   * 清除缓存
   */
  @CacheEvict(value = LABEL_MACHINE_UNIONS)
  public void clearCache() {}

  @CacheEvict(value = LABELS_FOR_USER, key = "#userId")
  public void clearCacheForUser(Long userId) {}

  /**
   * 在label上patch label所有的机器
   * @param labels
   * @return
   */
  public List<LabelMachineUnion> patchMachineForLabels(List<Label> labels) {
    List<LabelMachineUnion> labelMachineUnions = Lists.newLinkedList();
    labels.forEach(label -> {
      LabelMachineUnion labelMachineUnion = patchMachineForLabel(label);
      labelMachineUnions.add(labelMachineUnion);
    });

    return labelMachineUnions;
  }

  /**
   * 在label上patch label所有的机器
   * @param label
   * @return
   */
  private LabelMachineUnion patchMachineForLabel(Label label) {
    List<Machine> machines = machineLabelMapService.findMachinesForLabel(label.getId());
    LabelMachineUnion labelMachineUnion = new LabelMachineUnion();
    labelMachineUnion.setLabel(label);
    labelMachineUnion.setMachines(machines);

    return labelMachineUnion;
  }

  /**
   * 查找用户所拥有的所有label
   * @param userId
   * @return
   */
  public List<LabelMachineUnion> findLabelsForUser(Long userId) {
    List<LabelMachineUnion> labelMachineUnions = Lists.newLinkedList();
    if (SecurityUtils.getSubject().hasRole("admin")) {
      labelMachineUnions = findAll();
    } else {
      labelMachineUnions = findForUser(userId);
    }
    return labelMachineUnions;
  }

  /**
   * 查找用户有权限的所有label
   * @param userId
   * @return
   */
  @Cacheable(value = LABELS_FOR_USER, key = "#userId")
  private List<LabelMachineUnion> findForUser(Long userId) {
    // 用户有权限的label
    List<Label> labels = userLabelMapService.findLabelsForUser(userId);

    return patchMachineForLabels(labels);
  }
}
