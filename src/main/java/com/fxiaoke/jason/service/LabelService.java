package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.Label;
import com.fxiaoke.jason.mapper.LabelMapper;
import com.github.cas.CasClient;
import com.github.shiro.support.ShiroCasRealm;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alex on 16/9/28.
 */
@Service
public class LabelService {
  @Autowired
  private LabelMapper mapper;

  @Autowired
  private ShiroCasRealm shiroCasRealm;

  @Autowired
  private LabelMachineService labelMachineService;

  @Autowired
  private MachineLabelUnionService machineLabelUnionService;

  public CasClient casClient = CasClient.getInstance();

  public int insert(Label label) {
    return mapper.insert(label);
  }

  public int insertAndSetObjectId(Label label) {
    return mapper.insertAndSetObjectId(label);
  }

  public List<Label> findAll() {
    return mapper.findAll();
  }

  public List<Label> findLimit(String name, int pageNum, int pageSize, String order) {
    int offset = pageSize * (pageNum - 1);
    if (Strings.isNullOrEmpty(name)) {
      return mapper.findLimit(offset, pageSize, order);
    } else {
      return mapper.findLimitByName(name, offset, pageSize, order);
    }
  }

  public int findCount() {
    return mapper.findCount();
  }

  public Label findById(long id) { return mapper.findById(id); }

  public int update(Label label) { return mapper.update(label); }

  public Label findByName(String name) { return mapper.findByName(name); }

  public int deleteByName(String name) {
    return mapper.deleteByName(name);
  }

  public int deleteById(Long id) { return mapper.deleteById(id); }

  /**
   * 清空相关缓存
   */
  public void clearCache() {
    String userName = shiroCasRealm.getCurrentUserName();
    Long userId = casClient.findUidByName(userName);
    labelMachineService.clearLabelCacheForUser(userId);
    machineLabelUnionService.clearMachineLabelUnionsCacheForUser(userId);
  }
}
