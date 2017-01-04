package com.fxiaoke.jason.controller;

import com.alibaba.fastjson.JSON;
import com.fxiaoke.jason.entity.*;
import com.fxiaoke.jason.service.*;
import com.fxiaoke.jason.util.TextUtil;
import com.github.cas.CasClient;
import com.github.cas.User;
import com.github.shiro.support.ShiroCasRealm;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by alex on 16/9/27.
 */
@Controller
@Slf4j
@RequestMapping(value = "/label/")
public class LabelController {

  @Autowired
  private ShiroCasRealm shiroCasRealm;

  @Autowired
  private MachineService machineService;

  @Autowired
  private LabelService labelService;

  @Autowired
  private MachineLabelMapService machineLabelMapService;

  @Autowired
  private LabelMachineService labelMachineService;

  @Autowired
  private UserLabelMapService userLabelMapService;

  private CasClient casClient = CasClient.getInstance();

  /**
   * data table格式展现的label
   * @param model
   * @return
   */
  @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
  public String index(Model model) {
    String userName = shiroCasRealm.getCurrentUserName();
    Long userId = casClient.findUidByName(userName);

    List<LabelMachineUnion> labelMachineUnions = labelMachineService.findLabelsForUser(userId);

    model.addAttribute("labelMachineUnions", labelMachineUnions);

    return "label/index";
  }

  /**
   * ajax请求: 查找label下所有的机器
   * @param labelId
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/findMachinesForLabel/{labelId}", method = RequestMethod.GET)
  public String findMachinesForLabel(@PathVariable Long labelId) {
    List<Machine> machines = machineLabelMapService.findMachinesForLabel(labelId);
    return JSON.toJSONString(machines);
  }

  /**
   * 页面: 添加label
   * @return
   */
  @RequestMapping(value = "/add", method = RequestMethod.GET)
  public String addLabelGet() {
    return "label/add";
  }

  /**
   * 处理动作: 添加label
   * @param label
   * @param bind
   * @param attributes
   * @param model
   * @return
   */
  @RequestMapping(value = "/add", method = RequestMethod.POST)
  public String addLabelPost(@Valid Label label,
                             @RequestParam("machines") String machines,
                             @RequestParam("users") String users,
                             BindingResult bind,
                             RedirectAttributes attributes,
                             Model model) {

    if (bind.hasErrors()) {
      FieldError error = bind.getFieldError();
      String msg = MessageFormatter.format("{} 错误：{}", error.getField(), error.getDefaultMessage()).getMessage();
      attributes.addFlashAttribute("error", msg);
      return "redirect:/label/";
    }

    Label storedLabel = labelService.findByName(label.getNm());
    if (storedLabel != null) {
      attributes.addFlashAttribute("error", "label名字已被占用,请换其他名字。");
      return "redirect:/label/add";
    }

    labelService.insertAndSetObjectId(label);

    // 机器非空
    if (!Strings.isNullOrEmpty(machines)) {
      List<String> machineStrs = TextUtil.lineFeedToList(machines);
      machineStrs.forEach(host -> {

        Machine machine = machineService.insertIfNotExists(host);

        // 添加machine->label的映射
        MachineLabelMap machineLabelMap = new MachineLabelMap();
        machineLabelMap.setLabelId(label.getId());
        machineLabelMap.setMachineId(machine.getId());
        machineLabelMapService.insertIfNotExists(machineLabelMap);
      });
    }

    // 用户名非空
    if (!Strings.isNullOrEmpty(users)) {
      List<String> userStrs = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(users);

      // 添加user->label的映射
      userStrs.forEach(userStr -> {
        long userId = casClient.findUidByName(userStr);
        UserLabelMap userLabelMap = new UserLabelMap();
        userLabelMap.setLabelId(label.getId());
        userLabelMap.setUserId(userId);

        userLabelMapService.insert(userLabelMap);
      });
    }
    labelService.clearCache();

    model.addAttribute("success", "添加新标签成功。");

    return "redirect:/label/";
  }

  /**
   * 页面: 查看label
   * @param labelId
   * @param model
   * @return
   */
  @RequestMapping(value = "/detail/{labelId}", method = RequestMethod.GET)
  public String detailGet(@PathVariable Long labelId, Model model) {

    Label label = labelService.findById(labelId);
    model.addAttribute("label", label);

    List<Machine> machines = machineLabelMapService.findMachinesForLabel(label.getId());
    model.addAttribute("machines", machines);

    List<User> users = userLabelMapService.findUsersForLabel(label.getId());
    model.addAttribute("users", users);

    return "/label/detail";
  }

  /**
   * 页面: 编辑label
   * @param labelId
   * @param model
   * @return
   */
  @RequestMapping(value = "/edit/{labelId}", method = RequestMethod.GET)
  public String editLabelGet(@PathVariable Long labelId, Model model) {

    Label label = labelService.findById(labelId);
    model.addAttribute("label", label);

    List<Machine> machines = machineLabelMapService.findMachinesForLabel(label.getId());
    model.addAttribute("machines", machines);

    List<User> users = userLabelMapService.findUsersForLabel(label.getId());
    model.addAttribute("users", users);

    return "/label/edit";
  }

  /**
   * 处理动作: 编辑label
   * @param labelId
   * @param label
   * @param bind
   * @param attributes
   * @return
   */
  @RequestMapping(value = "/edit/{labelId}", method = RequestMethod.POST)
  public String editLabelPost(@PathVariable Long labelId,
                              @RequestParam("machines") String machines,
                              @RequestParam("users") String users,
                              @Valid Label label,
                              BindingResult bind,
                              RedirectAttributes attributes) {
    if (bind.hasErrors()) {
      FieldError error = bind.getFieldError();
      String msg = MessageFormatter.format("{} 错误：{}", error.getField(), error.getDefaultMessage()).getMessage();
      attributes.addFlashAttribute("error", msg);
      return "redirect:/label/";
    }

    // 更新label信息
    labelService.update(label);

    // 更新label绑定的机器
    if (!Strings.isNullOrEmpty(machines)) {
      // 已经绑定的机器
      List<Machine> storedMachines = machineLabelMapService.findMachinesForLabel(labelId);
      List<String> storedMachineHosts = Lists.newLinkedList();
      storedMachines.forEach(machine -> storedMachineHosts.add(machine.getHost()));

      List<String> machineStrs = TextUtil.lineFeedToList(machines);

      List<String> tmpMachineHosts = Lists.newLinkedList();
      tmpMachineHosts.addAll(storedMachineHosts);

      // 减少的host
      tmpMachineHosts.removeAll(machineStrs);
      tmpMachineHosts.forEach(machineHost -> {
        Machine machine = machineService.findByHost(machineHost);
        machineLabelMapService.deleteByMachineAndLabel(machine.getId(), labelId);
      });

      // 增加的host
      tmpMachineHosts.clear();
      tmpMachineHosts.addAll(machineStrs);
      tmpMachineHosts.removeAll(storedMachineHosts);
      tmpMachineHosts.stream().forEach(host -> {
        Machine machine = machineService.findByHost(host);

        if (machine == null) {
          machine = new Machine();
          machine.setHost(host);
          machineService.insertAndSetObjectId(machine);
        }

        MachineLabelMap machineLabelMap = new MachineLabelMap();
        machineLabelMap.setMachineId(machine.getId());
        machineLabelMap.setLabelId(label.getId());
        machineLabelMapService.insertAndSetObjectId(machineLabelMap);
      });
    }

    // 用户名非空
    if (!Strings.isNullOrEmpty(users)) {
      List<String> userNames = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(users);
      List<Long> userIds = Lists.newLinkedList();
      userNames.forEach(userName -> userIds.add(casClient.findUidByName(userName)) );

      // 已经绑定的用户
      List<UserLabelMap> storedUserLabelMaps = userLabelMapService.findByLabel(labelId);

      // 已经绑定的用户id
      List<Long> storedUserIds = Lists.newLinkedList();
      storedUserLabelMaps.forEach(storedUserLabelMap -> storedUserIds.add(storedUserLabelMap.getUserId()));

      List<Long> tmpUserIds = Lists.newLinkedList();
      tmpUserIds.addAll(storedUserIds);

      // 减少的user id
      tmpUserIds.removeAll(userIds);
      tmpUserIds.forEach(userId -> userLabelMapService.deleteByUserAndLabel(userId, labelId) );

      tmpUserIds.clear();
      tmpUserIds.addAll(userIds);
      tmpUserIds.removeAll(storedUserIds);
      // 添加user->label的映射
      tmpUserIds.forEach(userId -> {
        UserLabelMap userLabelMap = new UserLabelMap();
        userLabelMap.setLabelId(labelId);
        userLabelMap.setUserId(userId);

        userLabelMapService.insert(userLabelMap);
      });
    }

    labelService.clearCache();

    attributes.addFlashAttribute("success", "更新label成功");

    return "redirect:/label/";
  }

  /**
   * 处理动作: 根据id删除
   * @param labelId
   * @param attributes
   * @return
   */
  @RequestMapping(value = "/delete/{labelId}", method = RequestMethod.GET)
  public String delete(@PathVariable("labelId") Long labelId, RedirectAttributes attributes) {
    labelService.deleteById(labelId);
    labelService.clearCache();
    return "redirect:/label/";
  }

  @RequestMapping(value = "/conn/{labelId}")
  public String connectGet(@PathVariable("labelId") Long labelId, Model model) {
    Label label = labelService.findById(labelId);
    List<Machine> machines = machineLabelMapService.findMachinesForLabel(labelId);

    model.addAttribute("label", label);
    model.addAttribute("machines", machines);

    return "label/conn";
  }
}
