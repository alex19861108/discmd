package com.fxiaoke.jason.controller;

import com.fxiaoke.jason.entity.SchSession;
import com.fxiaoke.jason.entity.UserHttpSessions;
import com.fxiaoke.jason.entity.UserHttpSessionsFactory;
import com.fxiaoke.jason.entity.UserSchSessions;
import com.jcraft.jsch.ChannelShell;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by alex on 2016/10/10.
 */
@Slf4j
@Controller
@RequestMapping(value = "/terminal/")
public class TerminalController {

  private UserHttpSessions userHttpSessions = UserHttpSessionsFactory.getInstance();

  /**
   * 设置channel的尺寸
   * @param instanceId
   * @param ptyWidth
   * @param ptyHeight
   * @param request
   */
  @ResponseBody
  @RequestMapping(value = "/setPtySize")
  public void setPtySize(@RequestParam("instanceId") Integer instanceId,
                           @RequestParam("ptyWidth") Integer ptyWidth,
                           @RequestParam("ptyHeight") Integer ptyHeight,
                           @RequestParam("ptyCols") Integer ptyCols,
                           @RequestParam("ptyRows") Integer ptyRows,
                           HttpServletRequest request) {
    HttpSession httpSession = request.getSession();

    int tryTimes = 0;
    int maxTryTimes = 5;
    while (tryTimes < maxTryTimes) {
      log.warn(MessageFormatter.format("setPtySize for [instanceId]: {}, {}th times, instanceId: {}", instanceId, tryTimes).getMessage());
      UserSchSessions userSchSessions = userHttpSessions.get(httpSession.getId());
      if (userSchSessions != null) {
        SchSession schSession = userSchSessions.get(instanceId);
        if (schSession != null) {
          ChannelShell channelShell = (ChannelShell) schSession.getChannel();
          //channelShell.setPtySize((int) Math.floor(ptyWidth / 7.2981), (int) Math.floor(ptyHeight / 14.4166), ptyWidth, ptyHeight);
          channelShell.setPtySize(ptyCols, ptyRows, ptyWidth, ptyHeight);
          schSession.setChannel(channelShell);

          userSchSessions.put(instanceId, schSession);

          userHttpSessions.put(httpSession.getId(), userSchSessions);
        }
        break;
      }

      try {
        tryTimes++;
        Thread.sleep(1000);
      } catch (Exception e) {
        log.error(e.toString(), e);
      }
    }
  }
}
