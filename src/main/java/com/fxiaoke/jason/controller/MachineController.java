package com.fxiaoke.jason.controller;

import com.alibaba.fastjson.JSON;
import com.fxiaoke.jason.entity.*;
import com.fxiaoke.jason.service.*;
import com.fxiaoke.jason.util.TextUtil;
import com.github.cas.CasClient;
import com.github.shiro.support.ShiroCasRealm;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by alex on 16/9/26.
 */
@Controller
@Slf4j
@RequestMapping(value = "/machine/")
public class MachineController {

  @Autowired
  private ShiroCasRealm shiroCasRealm;

  @Autowired
  private MachineService machineService;

  @Autowired
  private LabelService labelService;

  @Autowired
  private MachineLabelMapService machineLabelMapService;

  @Autowired
  private MachineLabelUnionService machineLabelUnionService;

  @Autowired
  private UserLoginAccountService userLoginAccountService;

  @Autowired
  private SSHService sshService;

  @Autowired
  private DistributeJobService distributeJobService;

  @Autowired
  private JobService jobService;

  @Autowired
  private JobTaskMapService jobTaskMapService;

  @Autowired
  private SessionMachineStatusService sessionMachineStatusService;

  private UserHttpSessions userHttpSessions = UserHttpSessionsFactory.getInstance();

  private CasClient casClient;

  @PostConstruct
  void init() {
    casClient = CasClient.getInstance();
  }

  /**
   * 页面: data table格式, 带label信息的机器列表
   * @param model
   * @return
   */
  @RequestMapping(value = {"/", "index"}, method = RequestMethod.GET)
  public String index(Model model) {
    String userName = shiroCasRealm.getCurrentUserName();
    Long userId = casClient.findUidByName(userName);

    List<MachineLabelUnion> machineLabelUnions = machineLabelUnionService.findMachinesForUser(userId);
    List<UserLoginAccount> userLoginAccounts = userLoginAccountService.findByUser(userId);

    model.addAttribute("machineLabelUnions", machineLabelUnions);
    model.addAttribute("userLoginAccounts", userLoginAccounts);

    return "machine/index";
  }

  /**
   * 页面: 查看详情
   * @param machineId
   * @param model
   * @return
   */
  @RequestMapping(value = "/detail/{machineId}", method = RequestMethod.GET)
  public String machineDetailGet(@PathVariable int machineId, Model model) {
    Machine machine = machineService.findById(machineId);
    List<Label> labels = machineLabelMapService.findLabelsForMachine(machine.getId());

    model.addAttribute("machine", machine);
    model.addAttribute("labels", labels);

    return "machine/detail";
  }

  /**
   * 页面: 编辑
   * @param machineId
   * @param model
   * @return
   */
  @RequestMapping(value = "/edit/{machineId}", method = RequestMethod.GET)
  public String machineEditGet(@PathVariable Long machineId,
                               Model model) {
    Machine machine = machineService.findById(machineId);
    List<Label> labels = machineLabelMapService.findLabelsForMachine(machine.getId());

    model.addAttribute("machine", machine);
    model.addAttribute("labels", labels);

    return "machine/edit";
  }

  /**
   * 处理动作: 编辑
   * @param machineId
   * @param machine
   * @param belongToLabels
   * @param bindingResult
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = "/edit/{machineId}", method = RequestMethod.POST)
  public String machineEditPost(@PathVariable Long machineId,
                                @Valid Machine machine,
                                @RequestParam("belongToLabels") String belongToLabels,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      FieldError error = bindingResult.getFieldError();
      String msg = MessageFormatter.format("{} 错误：{}", error.getField(), error.getDefaultMessage()).getMessage();
      redirectAttributes.addFlashAttribute("error", msg);
      return "redirect:/label/";
    }

    // 字符串转换为list,返回值不可变
    List<String> labelStrs = TextUtil.lineFeedToList(belongToLabels);

    // 保存machine信息
    machineService.update(machine);

    // 已经保存的label
    List<Label> storedLabels = machineLabelMapService.findLabelsForMachine(machineId);
    List<String> storedLabelStrs = Lists.newLinkedList();
    storedLabels.stream().forEach(label -> storedLabelStrs.add(label.getNm()));

    // 临时list
    List<String> tmpList = Lists.newLinkedList();
    tmpList.addAll(storedLabelStrs);

    // 减少的label
    tmpList.removeAll(labelStrs);
    for (String labelName : tmpList) {
      Label label = labelService.findByName(labelName);
      if (label != null) {
        machineLabelMapService.deleteByMachineAndLabel(machineId, label.getId());
      }
    }

    StringBuffer errMsg = new StringBuffer();

    tmpList.clear();
    tmpList.addAll(labelStrs);
    // 新增的label
    tmpList.removeAll(storedLabelStrs);
    for (String labelName : tmpList) {
      Label label = labelService.findByName(labelName);
      if (label != null) {
        MachineLabelMap machineLabelMap = new MachineLabelMap();
        machineLabelMap.setMachineId(machineId);
        machineLabelMap.setLabelId(label.getId());
        machineLabelMapService.insertAndSetObjectId(machineLabelMap);
      } else {
        errMsg.append("不存在[name]:").append(labelName).append("的label.");
      }
    }

    if (!Strings.isNullOrEmpty(errMsg.toString())) {
      redirectAttributes.addAttribute("error", errMsg.toString());
    } else {
      redirectAttributes.addAttribute("success", "更新label成功");
    }

    return "redirect:/machine/detail/" + String.valueOf(machineId);
  }

  /**
   * 页面: 查看要导入的机器
   * @return
   */
  @RequestMapping(value = "/import", method = RequestMethod.GET)
  public String importMachineGet(Model model) {

    String bizInfoJSON = machineService.getBizInfo();
    Map<String, MachineLabelUnion> machineLabelUnionMap = machineService.getImportMachines(bizInfoJSON);

    model.addAttribute("machineLabelUnions", machineLabelUnionMap.values());

    return "machine/import";
  }

  /**
   * 处理动作: 导入机器
   * @return
   */
  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public String importMachinePost(RedirectAttributes attributes) {

    // 从发布系统获取的json格式的biz信息
    String bizInfoJSON = machineService.getBizInfo();

    machineService.importMachines(bizInfoJSON);

    attributes.addFlashAttribute("success", "导入机器完成.");

    return "redirect:/machine/";
  }

  /**
   * 页面: 文件上传
   * @param model
   * @return
   */
  @RequestMapping(value = "/deploy", method = RequestMethod.GET)
  public String deployGet(Model model) {

    String userName = shiroCasRealm.getCurrentUserName();
    Long userId = casClient.findUidByName(userName);

    List<MachineLabelUnion> machineLabelUnions = machineLabelUnionService.findMachinesForUser(userId);

    // 登陆账户
    List<UserLoginAccount> userLoginAccounts = userLoginAccountService.findByUser(userId);

    model.addAttribute("machineLabelUnions", machineLabelUnions);
    model.addAttribute("userLoginAccountSize", userLoginAccounts.size());
    model.addAttribute("userLoginAccounts", userLoginAccounts);

    return "machine/deploy";
  }

  /**
   * 处理动作: 文件上传
   * @param machineIds
   * @param destination
   * @param accountId
   * @param request
   * @param model
   * @return
   */
  @RequestMapping(value = "/deploy", method = RequestMethod.POST)
  public String deployPost(@RequestParam(value = "machine_ids", defaultValue = "") List<Long> machineIds,
                           @RequestParam(value = "destination", defaultValue = "~") String destination,
                           @RequestParam(value = "account_id", defaultValue = "") Long accountId,
                           RedirectAttributes attributes,
                           HttpServletRequest request,
                           Model model) {

    if (machineIds.isEmpty()) {
      attributes.addFlashAttribute("error", "请选择发布机器。");
      return "redirect:/machine/deploy";
    }

    String userName = shiroCasRealm.getCurrentUserName();
    Long userId = casClient.findUidByName(userName);

    // 登陆账户
    UserLoginAccount userLoginAccount = userLoginAccountService.findById(accountId);

    if (userLoginAccount.getUserId() != userId) {
      attributes.addFlashAttribute("error", "请不要盗用其他人的账户");
      return "redirect:/error";
    }


    Long jobId = distributeJobService.upload(request, machineIds, destination, userLoginAccount);

    model.addAttribute("jobId", jobId);

    return "redirect:/machine/deploy_result/" + String.valueOf(jobId);
  }

  /**
   * 页面: 发布结果
   * @param jobId
   * @param model
   * @return
   */
  @RequestMapping(value = "/deploy_result/{jobId}", method = RequestMethod.GET)
  public String deployResult(@PathVariable("jobId") Long jobId, Model model) {

    Job job = jobService.findById(jobId);

    List<Task> tasks = jobTaskMapService.findTasksForJob(jobId);

    String jobStatus = Job.SUCCESS;
    for (Task task : tasks) {
      if (task.getStatusCd().equals(Job.RUNNING)) {
        jobStatus = Job.RUNNING;
        break;
      } else if (task.getStatusCd().equals(Job.FAILED)) {
        jobStatus = Job.FAILED;
        break;
      } else if (task.getStatusCd().equals(Job.PENDING)) {
        jobStatus = Job.PENDING;
        break;
      } else if (!task.getStatusCd().equals(Job.SUCCESS)) {
        jobStatus = Job.FAILED;
        break;
      }
    }
    job.setStatusCd(jobStatus);
    job.setEndDt(DateTime.now().toDate());
    jobService.update(job);


    model.addAttribute("tasks", tasks);
    model.addAttribute("job", job);

    return "machine/deploy_result";
  }

  /**
   * 查询job状态
   * @param jobId
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/deploy/jobStatus", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
  public String deployStatus(@RequestParam("jobId") Long jobId) {
    Job job = jobService.findById(jobId);
    List<Task> tasks = jobTaskMapService.findTasksForJob(jobId);
    String jobStatus = Job.SUCCESS;
    for (Task task : tasks) {
      if (task.getStatusCd().equals(Job.RUNNING)) {
        jobStatus = Job.RUNNING;
        break;
      } else if (task.getStatusCd().equals(Job.FAILED)) {
        jobStatus = Job.FAILED;
        break;
      } else if (task.getStatusCd().equals(Job.PENDING)) {
        jobStatus = Job.PENDING;
        break;
      } else if (!task.getStatusCd().equals(Job.SUCCESS)) {
        jobStatus = Job.FAILED;
        break;
      }
    }
    job.setStatusCd(jobStatus);
    job.setEndDt(DateTime.now().toDate());
    jobService.update(job);

    return JSON.toJSONString(tasks);
  }

  /**
   * 页面: websocket方式,多terminal交互
   * @param machineIds
   * @param request
   * @param model
   * @return
   */
  @RequestMapping(value = "/terminal/customize/by_id", method = RequestMethod.GET)
  public String customizeTerminal(@RequestParam(value = "machine_ids", defaultValue = "") List<Long> machineIds,
                                  @RequestParam(value = "account_id", defaultValue = "") Long accountId,
                                  HttpServletRequest request,
                                  RedirectAttributes attributes,
                                  Model model) {
    if (machineIds.isEmpty()) {
      return "redirect:/error";
    }

    //TODO: 增加校验，不能随便修改参数，使用别人的账户，或者登录别人的机器

    // 当前用户
    String userName = shiroCasRealm.getCurrentUser().getDisplayName();
    Long userId = casClient.findUidByName(userName);

    // 登陆账户
    UserLoginAccount userLoginAccount = userLoginAccountService.findById(accountId);

    if (userLoginAccount.getUserId() != userId) {
      attributes.addFlashAttribute("error", "请不要盗用其他人的账户");
      return "redirect:/error";
    }

    // sessionId
    HttpSession session = request.getSession();
    String sessionId = session.getId();

    // machines
    List<Machine> machines = machineService.findByIds(machineIds);

    machines.stream().forEach(machine -> {
      log.info("{} login {}, using account: {}", userName, machine.getHost(), userLoginAccount);
      // 建立连接
      sshService.openSSHTermOnMachine(machine, userLoginAccount, sessionId, userHttpSessions);
    });

    // 创建连接错误提示
    StringBuilder sbd = new StringBuilder();
    machines.stream().filter(mac -> !Objects.equals(mac.getStatusCd(), Machine.SUCCESS_STATUS)).forEach(mac ->
      sbd.append(MessageFormatter.format("{}, {}\n", mac.getHost(), mac.getErrorMsg()).getMessage())
    );

    if (sbd.length() > 0) {
      model.addAttribute("warning", sbd.toString());
    }

    model.addAttribute("machines", machines);

    return "machine/terminal_sync";
  }

  /**
   * 页面: websocket方式,多terminal交互
   * @param machineIds
   * @param request
   * @param model
   * @return
   */
  @RequestMapping(value = "/terminal/async/by_id", method = RequestMethod.GET)
  public String createTerminalAsync(@RequestParam(value = "machine_ids", defaultValue = "") List<Long> machineIds,
                                  @RequestParam(value = "account_id", defaultValue = "") Long accountId,
                                  HttpServletRequest request,
                                  RedirectAttributes attributes,
                                  Model model) {
    if (machineIds.isEmpty()) {
      return "redirect:/error";
    }

    // 当前用户
    String userName = shiroCasRealm.getCurrentUser().getDisplayName();
    Long userId = casClient.findUidByName(userName);

    // 登陆账户
    UserLoginAccount userLoginAccount = userLoginAccountService.findById(accountId);

    if (userLoginAccount.getUserId() != userId) {
      attributes.addFlashAttribute("error", "请不要盗用其他人的账户");
      return "redirect:/error";
    }

    // sessionId
    HttpSession session = request.getSession();
    String sessionId = session.getId();

    // machines
    List<Machine> machines = machineService.findByIds(machineIds);

    int instanceId = 0;
    for (Machine machine : machines) {
      machine.setInstanceId(++instanceId);
      sshService.openSSHTermOnMachineAsync(machine, userLoginAccount, sessionId, userHttpSessions);
    }

    model.addAttribute("machines", machines);
    model.addAttribute("machinesJSON", JSON.toJSONString(machines));
    model.addAttribute("userLoginAccount", JSON.toJSONString(userLoginAccount));

    return "machine/terminal_async";
  }

  /**
   * 查看terminal状态
   * @param request
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/terminal/status", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
  public String getTerminalConnectionStatus(HttpServletRequest request) {
    // sessionId
    HttpSession session = request.getSession();
    String sessionId = session.getId();

    List<SessionMachineStatus> sessionMachineStatuses = sessionMachineStatusService.findBySession(sessionId);
    return JSON.toJSONString(sessionMachineStatuses);
  }

  /**
   * 页面: 创建terminal之前的处理动作
   * @param hosts
   * @param model
   * @return
   */
  @RequestMapping(value = "/terminals/by_host", method = RequestMethod.GET)
  public String beforeCreateTerms(@RequestParam(value = "hosts", defaultValue = "") List<String> hosts,
                                  RedirectAttributes attributes,
                                  Model model) {
    // 当前用户
    String userName = shiroCasRealm.getCurrentUser().getDisplayName();
    long userId = casClient.findUidByName(userName);

    // 登陆账户
    List<UserLoginAccount> userLoginAccounts = userLoginAccountService.findByUser(userId);

    // 登陆账户为空
    if (userLoginAccounts.isEmpty()) {
      attributes.addAttribute("warning", "使用前请添加登陆账户");
      return "redirect:/user/add/";
    }

    List<Long> machineIds = Lists.newLinkedList();
    try {
      hosts.forEach(host -> {
        Machine machine = machineService.findByHost(host);
        machineIds.add(machine.getId());
      });
    } catch (Exception e) {
      log.error("machineService.findByHost(), hosts: {}", hosts, e);
      attributes.addFlashAttribute("error", "使用前请先在label上注册机器");
      return "redirect:/label/";
    }

    model.addAttribute("userLoginAccountSize", userLoginAccounts.size());
    model.addAttribute("userLoginAccounts", userLoginAccounts);
    model.addAttribute("machineIds", machineIds);

    return "machine/before_create_terms";
  }

  /**
   * 页面: 创建terminal之前的处理动作
   * @param machineIds
   * @param model
   * @return
   */
  @RequestMapping(value = "/terminals/by_id", method = RequestMethod.GET)
  public String beforeCreateTermsById(@RequestParam(value = "machine_ids", defaultValue = "") List<Long> machineIds,
                                      RedirectAttributes attributes,
                                      Model model) {
    // 当前用户
    String userName = shiroCasRealm.getCurrentUser().getDisplayName();
    long userId = casClient.findUidByName(userName);

    // 登陆账户
    List<UserLoginAccount> userLoginAccounts = userLoginAccountService.findByUser(userId);

    // 登陆账户为空
    if (userLoginAccounts.isEmpty()) {
      attributes.addFlashAttribute("warning", "使用前请添加登陆账户");
      return "redirect:/user/add/";
    }

    model.addAttribute("userLoginAccountSize", userLoginAccounts.size());
    model.addAttribute("userLoginAccounts", userLoginAccounts);
    model.addAttribute("machineIds", machineIds);

    return "machine/before_create_terms";
  }
}
