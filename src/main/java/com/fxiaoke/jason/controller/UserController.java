package com.fxiaoke.jason.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fxiaoke.jason.entity.UserLoginAccount;
import com.fxiaoke.jason.service.UserLoginAccountService;
import com.fxiaoke.jason.util.EncryptionUtil;
import com.github.cas.CasClient;
import com.github.cas.User;
import com.github.shiro.support.ShiroCasRealm;
import com.github.shiro.support.ShiroUser;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * Created by alex on 16/9/28.
 */
@Controller
@Slf4j
@RequestMapping(value = "/user/")
public class UserController {

  @Autowired
  private ShiroCasRealm shiroCasRealm;

  @Autowired
  private UserLoginAccountService userLoginAccountService;

  private CasClient casClient = CasClient.getInstance();

  @ResponseBody
  @RequestMapping(value = "/findUserByName", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
  public ResponseEntity<String> findUserByName(@RequestParam String name) {
    JSONArray users = null;
    try {
      users = casClient.findUsersByName(name);
      return new ResponseEntity<>(users.toString(), HttpStatus.OK);
    } catch (IOException e) {
      log.error("findUsersByName({})", name, e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 页面: 用户列表
   * @return
   */
  @RequestMapping(value = {"/", "index"}, method = RequestMethod.GET)
  public String index(Model model) {
    List<UserLoginAccount> userLoginAccounts = userLoginAccountService.findAll();
    List<UserLoginAccount> users = Lists.newLinkedList();
    userLoginAccounts.forEach(userLoginAccount -> {
      try {
        String jsonStr = casClient.findUserById(userLoginAccount.getUserId());
        User user = JSON.parseObject(jsonStr, User.class);
        userLoginAccount.setRealname(user.getRealname());
        userLoginAccount.setUsername(user.getUsername());
        users.add(userLoginAccount);
      } catch (IOException e) {
        log.error("call findUserById failed. userId: " + userLoginAccount.getUserId(), e);
      }
    });

    model.addAttribute("users", users);

    return "user/index";
  }

  /**
   * 页面: 用户注册
   * @return
   */
  @RequestMapping(value = "/add", method = RequestMethod.GET)
  public String addGet(Model model) {

    ShiroUser shiroUser = shiroCasRealm.getCurrentUser();
    Long userId = casClient.findUidByName(shiroUser.getUsername());

    String userName = shiroUser.getDisplayName();

    model.addAttribute("userName", userName);
    model.addAttribute("userId", userId);

    return "user/add";
  }

  /**
   * 页面: 用户查看自己的权限
   * @param model
   * @return
   */
  @RequestMapping(value = "/owned_account", method = RequestMethod.GET)
  public String ownedAccount(Model model) {
    String userName = shiroCasRealm.getCurrentUserName();
    Long userId = casClient.findUidByName(userName);

    List<UserLoginAccount> userLoginAccounts = userLoginAccountService.findByUser(userId);

    model.addAttribute("userLoginAccounts", userLoginAccounts);

    return "user/owned_account";
  }

  /**
   * 页面: 用户修改自己的密码
   * @param model
   * @return
   */
  @RequestMapping(value = "/account/edit/{id}", method = RequestMethod.GET)
  public String editAccountGet(@PathVariable(value = "id") Long id, Model model) {

    UserLoginAccount userLoginAccount = userLoginAccountService.findById(id);

    model.addAttribute("userLoginAccount", userLoginAccount);

    return "user/edit";
  }

  /**
   * 处理动作: 用户修改自己的密码
   * @param attributes
   * @return
   */
  @RequestMapping(value = "/account/edit/{id}", method = RequestMethod.POST)
  public String editAccountPost(@PathVariable(value = "id") Long id,
                                @Valid UserLoginAccount userLoginAccount,
                                BindingResult bindingResult,
                                RedirectAttributes attributes) {
    if (bindingResult.hasErrors()) {
      FieldError error = bindingResult.getFieldError();
      String msg = MessageFormatter.format("{} 错误：{}", error.getField(), error.getDefaultMessage()).getMessage();
      attributes.addFlashAttribute("error", msg);
      return "redirect:/user/owned_account/";
    }

    String encryptPassword = EncryptionUtil.encrypt(userLoginAccount.getPassword());
    userLoginAccount.setPassword(encryptPassword);
    userLoginAccountService.update(userLoginAccount);

    attributes.addFlashAttribute("success", "更新登陆账户成功");

    return "redirect:/user/owned_account/";
  }

  /**
   * 处理动作: 用户注册
   * @param userId
   * @param password
   * @param attributes
   * @return
   */
  @RequestMapping(value = "/add", method = RequestMethod.POST)
  public String addPost(@RequestParam("userId") Long userId,
                             @RequestParam("account") String account,
                             @RequestParam("password") String password,
                             RedirectAttributes attributes) {

    account = account.trim();
    password = password.trim();

    if (Strings.isNullOrEmpty(password) || Strings.isNullOrEmpty(account)) {
      attributes.addFlashAttribute("error", "账户密码不能为空!");
      return "redirect:/user/add";
    }

    boolean isExists = false;
    List<UserLoginAccount> userLoginAccounts = userLoginAccountService.findByUser(userId);
    for (UserLoginAccount userLoginAccount : userLoginAccounts) {
      if (userLoginAccount.getAccount().equals(account)) {
        userLoginAccount.setPassword(EncryptionUtil.encrypt(password));
        userLoginAccountService.update(userLoginAccount);
        isExists = true;
      }
    }

    if (!isExists) {
      UserLoginAccount userLoginAccount = new UserLoginAccount();
      userLoginAccount.setUserId(userId);
      userLoginAccount.setAccount(account);
      userLoginAccount.setPassword(EncryptionUtil.encrypt(password));
      userLoginAccountService.insert(userLoginAccount);
      attributes.addAttribute("success", "成功绑定密码!");
    } else {
      attributes.addFlashAttribute("success", "成功更新密码");
    }

    return "redirect:/user/owned_account";
  }

  /**
   * 删除用户
   * @param id
   * @param attributes
   * @return
   */
  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  public String delete(@RequestParam("id") Long id, RedirectAttributes attributes) {
    userLoginAccountService.delete(id);

    attributes.addFlashAttribute("success", "删除用户成功");
    return "redirect:/user/owned_account";
  }
}
