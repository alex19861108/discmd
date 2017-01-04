package com.fxiaoke.jason.task;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alex on 2016/10/11.
 */
@Getter
@Setter
public class SentOutputTask implements Runnable {

  private static Logger log = LoggerFactory.getLogger(SentOutputTask.class);

  private WebSocketSession session;
  private InputStream inputStream;
  private Long sessionId;

  public SentOutputTask(WebSocketSession session, InputStream inputStream) {
    this.sessionId = Long.valueOf(session.getId());
    this.session = session;
    this.inputStream = inputStream;
  }

  @Override
  public void run() {

    InputStreamReader isr = new InputStreamReader(inputStream);
    try {
      char[] buff = new char[1024];
      int read;
      while ((read = isr.read(buff)) != -1) {
        String s = new String(buff, 0, read);
        if (!Strings.isNullOrEmpty(s)) {
          session.sendMessage(new TextMessage(s));
        }
      }
    } catch (Exception e) {
      log.error(e.toString(), e);
    }

    /**
    while (session.isOpen()) {
      List<SessionOutput> outputList = SessionOutputUtil.getOutput(sessionId);
      try {
        if (outputList != null && !outputList.isEmpty()) {
          String json = gson.toJson(outputList);
          //send json to session
          session.sendMessage(new TextMessage(json));
        }
        Thread.sleep(50);
      } catch (Exception ex) {
        log.error("run() error: {}", ex.toString(), ex);
      }
    }
     */
  }
}
