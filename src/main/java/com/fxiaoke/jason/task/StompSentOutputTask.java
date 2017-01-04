package com.fxiaoke.jason.task;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alex on 2016/10/21.
 */
@Getter
@Setter
public class StompSentOutputTask implements Runnable {

  private static Logger log = LoggerFactory.getLogger(StompSentOutputTask.class);

  private final SimpMessagingTemplate simpMessagingTemplate;

  private InputStream inputStream;

  private String destination;

  public StompSentOutputTask(InputStream inputStream, String destination, SimpMessagingTemplate simpMessagingTemplate) {
    this.inputStream = inputStream;
    this.destination = destination;
    this.simpMessagingTemplate = simpMessagingTemplate;
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
          simpMessagingTemplate.convertAndSend(destination, s);
        }
      }
    } catch (Exception e) {
      log.error(e.toString(), e);
    }
  }

}
