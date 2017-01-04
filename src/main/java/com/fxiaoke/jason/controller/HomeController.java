package com.fxiaoke.jason.controller;

import com.fxiaoke.jason.service.ConfigService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by alex on 2016/10/13.
 */
@Controller
public class HomeController {

  @Autowired
  private ConfigService configService;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index() {

    return "redirect:/machine/";
  }

  @RequestMapping(value = "/error", method = RequestMethod.GET)
  public String error() {
    return "home/error";
  }

  @RequestMapping("/logout")
  public String logout() {
    SecurityUtils.getSubject().logout();
    return "redirect:" + configService.getCasServerUrlPrefix() + "/logout";
  }
}
