package com.fxiaoke.jason.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Created by alex on 2016/10/11.
 */
@Slf4j
public class SystemTextWebSocketHandler extends TextWebSocketHandler {

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

    // 获取提交过来的消息
    String text = message.getPayload();
    log.error("handle message: " + text);

    // 数据返回
    session.sendMessage(message);
  }
}
