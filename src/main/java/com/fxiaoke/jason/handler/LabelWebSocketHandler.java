package com.fxiaoke.jason.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fxiaoke.jason.entity.*;
import com.fxiaoke.jason.service.SessionLogService;
import com.fxiaoke.jason.task.MultiSendOutputTask;
import com.fxiaoke.jason.util.SessionOutputUtil;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.io.PrintStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alex on 2016/10/24.
 */
@Slf4j
public class LabelWebSocketHandler implements WebSocketHandler {

  private UserHttpSessions userHttpSessions = UserHttpSessionsFactory.getInstance();

  @Autowired
  private SessionLogService sessionLogService;

  @Override
  public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {

    log.debug("[label webSocket]: after connection established.");

    String sessionId = getSessionId(webSocketSession);
    log.debug("sessionId: " + sessionId);

    // 创建session log记录
    String userName = ((StandardWebSocketSession) webSocketSession).getNativeSession().getUserPrincipal().getName();
    SessionLog sessionLog = new SessionLog();
    sessionLog.setUserName(userName);
    sessionLog.setSessionId(sessionId);
    sessionLog.setBeginDt(DateTime.now().toDate());
    sessionLog.setStatusCd(SessionLog.RUNNING);
    sessionLogService.insertAndSetObjectId(sessionLog);


    if (!Strings.isNullOrEmpty(sessionId)) {
      Runnable run = new MultiSendOutputTask(webSocketSession, sessionId);
      Thread thread = new Thread(run);
      thread.start();
    } else {
      webSocketSession.sendMessage(new TextMessage("Build Connection failed. Error msg: session is empty"));
      sessionLog.setEndDt(DateTime.now().toDate());
      sessionLog.setStatusCd(SessionLog.ERROR);
      sessionLog.setErrorMsg("Build Connection failed. Error msg: session is empty");
      sessionLogService.update(sessionLog);
    }
  }

  @Override
  public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
    log.debug("[label webSocket]: handle message.");

    String sessionId = getSessionId(webSocketSession);
    log.debug("sessionId: " + sessionId);

    JSONObject obj = JSON.parseObject(webSocketMessage.getPayload().toString());
    // id : 选中的terminal实例
    // command : 输入的命令
    JSONArray idArray = obj.getJSONArray("id");
    String command = obj.getString("command");
    idArray.forEach(id -> {
      PrintStream commander = userHttpSessions.get(sessionId).get(Integer.parseInt(id.toString())).getCommander();
      commander.print(command);
      commander.flush();
    });
  }

  /**
   * 从WebSocketSession中提取session
   * @param webSocketSession
   * @return
   */
  public String getSessionId(WebSocketSession webSocketSession) {
    String sessionId = "";
    String cookie = webSocketSession.getHandshakeHeaders().get("cookie").get(0);
    String reg = "JSESSIONID=(\\w*)";
    Pattern ptn = Pattern.compile(reg);
    Matcher matcher = ptn.matcher(cookie);
    if (matcher.find()) {
      sessionId = matcher.group(1);
    }
    return sessionId;
  }

  @Override
  public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

  }

  @Override
  public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
    log.debug("[label webSocket]: after connection closed.");

    String sessionId = getSessionId(webSocketSession);
    log.debug("sessionId: " + sessionId);

    if (userHttpSessions.keySet().contains(sessionId)) {
      UserSchSessions userSchSessions = userHttpSessions.get(sessionId);
      if (userSchSessions != null) {
        for (Map.Entry<Integer, SchSession > entry : userSchSessions.entrySet()) {
          SchSession schSession = entry.getValue();

          schSession.getChannel().disconnect();
          schSession.getSession().disconnect();
          schSession.setChannel(null);
          schSession.setSession(null);
          schSession.setInputToChannel(null);
          schSession.setChannel(null);
          schSession.setOutFromChannel(null);

          schSession = null;

          userSchSessions.remove(entry.getKey());
        }
        userSchSessions.clear();

        // session log
        SessionLog sessionLog = sessionLogService.findBySession(sessionId);
        sessionLog.setEndDt(DateTime.now().toDate());
        sessionLog.setStatusCd(SessionLog.CLOSED);
        sessionLogService.update(sessionLog);
      } else {
        // session log
        SessionLog sessionLog = sessionLogService.findBySession(sessionId);
        sessionLog.setEndDt(DateTime.now().toDate());
        sessionLog.setStatusCd(SessionLog.ERROR);
        sessionLog.setErrorMsg(MessageFormatter.format("don't have sessionId: {}", sessionId).getMessage());
        sessionLogService.update(sessionLog);
      }
      userHttpSessions.remove(sessionId);
    }

    SessionOutputUtil.removeUserSession(sessionId);
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
