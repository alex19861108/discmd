package com.fxiaoke.jason.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fxiaoke.jason.entity.*;
import com.fxiaoke.jason.mapper.MachineMapper;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.github.cas.CasClient;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by alex on 16/9/26.
 */
@Slf4j
@Service
public class MachineService {

  private static Logger logger = LoggerFactory.getLogger(MachineService.class);

  @Autowired
  private MachineMapper mapper;

  @Autowired
  private LabelService labelService;

  @Autowired
  private MachineLabelMapService machineLabelMapService;

  @Autowired
  private MachineLabelUnionService machineLabelUnionService;

  @Autowired
  private UserLabelMapService userLabelMapService;

  private CasClient casClient = CasClient.getInstance();

  @ReloadableProperty("publishServerURL")
  private String publishServerURL;

  private OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(6, TimeUnit.SECONDS).readTimeout(3, TimeUnit.MINUTES).build();

  public List<Machine> findAll() {
    return mapper.findAll();
  }

  public Machine findById(long id) { return mapper.findById(id); }

  public Machine findByHost(String host) {
    return mapper.findByHost(host);
  }

  public int insertAndSetObjectId(Machine machine) {
    return mapper.insertAndSetObjectId(machine);
  }

  public int update(Machine machine) {
    machineLabelUnionService.clearCache();
    return mapper.update(machine);
  }

  /**
   * 如果不存在则创建
   * @param host
   * @return
   */
  public Machine insertIfNotExists(String host) {
    Machine machine = findByHost(host);
    if (machine == null) {
      // 新建机器
      machine = new Machine();
      machine.setHost(host);
      insertAndSetObjectId(machine);
    }
    return machine;
  }

  private Request.Builder getBuilder() {
    Request.Builder request = new Request.Builder().addHeader("Accept", "text/html")
                                                   .addHeader("Accept-Language", "en-US,en;q=0.8")
                                                   .addHeader("Cache-Control", "max-age=0")
                                                   .addHeader("Connection", "keep-alive")
                                                   .addHeader("Content-Type", "text/plain; charset=utf-8")
                                                   .addHeader("Accept-Encoding", "identity");        // 强制不使用gzip
    return request;
  }


  /**
   * 获取biz信息
   * @return
   */
  public String getBizInfo() {
    String bizInfo = "";

    String bizInfoURL = publishServerURL + "/biz/bizInfoJSON";
    Request request = getBuilder().url(bizInfoURL).build();

    try {
      Response response = httpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        bizInfo = response.body().string();
      }
    } catch (Exception e) {
      logger.error("okhttp request failed. [url]: {}, [error]: {}", bizInfoURL, e);
    }

    return bizInfo;
  }

  /**
   * 获取从publish server导入的机器
   * @return
   */
  public Map<String, MachineLabelUnion> getImportMachines(String bizInfoJSON) {

    // 将要import的机器列表
    Map<String, MachineLabelUnion> importMachineLabelUnionMap = Maps.newHashMap();

    JSONArray jsonArray = JSON.parseArray(bizInfoJSON);
    for (Object obj : jsonArray) {
      String labelNm = (String) ((JSONObject) obj).get("name");
      JSONArray apps = (JSONArray) ((JSONObject) obj).get("apps");
      for (Object app : apps) {
        String host = ((JSONObject) app).get("ip").toString();

        boolean isExists = false;
        Machine machine = findByHost(host);
        if (machine != null) {
          Label label = labelService.findByName(labelNm);
          if (label != null) {
            MachineLabelMap machineLabelMap = machineLabelMapService.findByMachineAndLabel(machine.getId(), label.getId());
            if (machineLabelMap != null) {
              isExists = true;
            }
          }
        }
        if (!isExists) {
          addToMachineLabelUnionMapIfNotExists(importMachineLabelUnionMap, host, labelNm);
        }
      }
    }

    return importMachineLabelUnionMap;
  }

  /**
   * 将机器信息添加到map中
   * @param importMachineLabelUnionMap
   * @param host
   * @param labelNm
   */
  public void addToMachineLabelUnionMapIfNotExists(Map<String, MachineLabelUnion> importMachineLabelUnionMap, String host, String labelNm) {
    // 数据库中不存在label
    Label label = new Label();
    label.setNm(labelNm);
    label.setDesc(labelNm);

    MachineLabelUnion machineLabelUnion = null;
    if (importMachineLabelUnionMap.keySet().contains(host)) {
      machineLabelUnion = importMachineLabelUnionMap.get(host);
    } else {
      machineLabelUnion = new MachineLabelUnion();
      Machine machine = new Machine();
      machine.setHost(host);
      machineLabelUnion.setMachine(machine);
    }
    List<Label> labels = machineLabelUnion.getLabels();
    if (labels == null) {
      labels = Lists.newLinkedList();
    }
    labels.add(label);
    machineLabelUnion.setLabels(labels);

    importMachineLabelUnionMap.put(host, machineLabelUnion);
  }

  /**
   * 导入机器
   * @param bizInfoJSON
   */
  public void importMachines(String bizInfoJSON) {
    // 往DB中插入label信息、以及其他映射关系
    JSONArray jsonArray = JSON.parseArray(bizInfoJSON);
    for (Object obj : jsonArray) {
      String projectName = ((JSONObject) obj).get("name").toString();
      String projectId = ((JSONObject) obj).get("projectId").toString();
      JSONArray apps = ((JSONObject) obj).getJSONArray("apps");

      // 保存label
      Label label = labelService.findByName(projectName);
      if (label == null) {
        label = new Label();
        label.setNm(projectName);
        label.setDesc(projectName);
        labelService.insertAndSetObjectId(label);
      }

      // 保存user->label的映射关系
      String projectInfo = getProjectInfo(projectId);
      JSONArray projectJsonArray = JSON.parseArray(projectInfo);
      for (Object projectObj : projectJsonArray) {
        String userName = ((JSONObject) projectObj).get("name").toString().replaceAll(" ", "");
        Long userId = casClient.findUidByName(userName);
        if (userId == 0L) {
          log.error("findUidByName return 0. username: " + userName);
          continue;
        }

        UserLabelMap userLabelMap = userLabelMapService.findByUserAndLabel(userId, label.getId());
        if (userLabelMap == null) {
          userLabelMap = new UserLabelMap();
          userLabelMap.setLabelId(label.getId());
          userLabelMap.setUserId(userId);
          userLabelMapService.insert(userLabelMap);
        }
      }

      for (Object app : apps) {

        String host = ((JSONObject) app).get("ip").toString();

        // 保存machine
        Machine machine = findByHost(host);
        if (machine == null) {
          machine = new Machine();
          machine.setHost(host);
          insertAndSetObjectId(machine);
        }

        // 保存machine->label的映射关系
        MachineLabelMap machineLabelMap = machineLabelMapService.findByMachineAndLabel(machine.getId(), label.getId());
        if (machineLabelMap == null) {
          machineLabelMap = new MachineLabelMap();
          machineLabelMap.setLabelId(label.getId());
          machineLabelMap.setMachineId(machine.getId());
          machineLabelMapService.insertAndSetObjectId(machineLabelMap);
        }
      }
    }

    labelService.clearCache();
  }

  /**
   * 根据project id获取用户名
   * @param projectId
   * @return
   */
  public String getProjectInfo(String projectId) {

    String responseStr = "";

    String url = publishServerURL + "/biz/projectUsersJSON?projectId=" + projectId;
    Request request = getBuilder().url(url).build();

    try {
      Response response = httpClient.newCall(request).execute();
      responseStr = response.body().string();

    } catch (Exception e) {
      logger.error("okhttp request failed. [url]: {}, [error]: {}", url, e);
    }

    return responseStr;
  }

  /**
   * 根据机器id列表,批量查找
   * @param machineIds
   * @return
   */
  public List<Machine> findByIds(List<Long> machineIds) {
    String ids = Joiner.on(",").join(machineIds);
    return mapper.findByIds(ids);
  }
}
