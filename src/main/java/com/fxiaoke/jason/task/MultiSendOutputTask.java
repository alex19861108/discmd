package com.fxiaoke.jason.task;

import com.alibaba.fastjson.JSON;
import com.fxiaoke.jason.entity.SessionOutput;
import com.fxiaoke.jason.util.SessionOutputUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * Created by alex on 2016/10/25.
 */
@Slf4j
public class MultiSendOutputTask implements Runnable {

  private String sessionId;
  private WebSocketSession session;

  public MultiSendOutputTask(WebSocketSession session, String sessionId) {
    this.session = session;
    this.sessionId = sessionId;
  }

  @Override
  public void run() {
    while (session.isOpen()) {
      List<SessionOutput> outputList = SessionOutputUtil.getOutput(sessionId);
      try {
        if (outputList != null && !outputList.isEmpty()) {
          String json = JSON.toJSONString(outputList);
          session.sendMessage(new TextMessage(json));
        }
        Thread.sleep(50);
      } catch (Exception ex) {
        log.error("run() error: {}", ex.toString(), ex);
      }
    }
  }
}
