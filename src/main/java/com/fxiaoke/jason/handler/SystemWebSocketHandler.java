package com.fxiaoke.jason.handler;

import com.fxiaoke.jason.entity.ApplicationKey;
import com.fxiaoke.jason.entity.Machine;
import com.fxiaoke.jason.service.MachineService;
import com.fxiaoke.jason.service.SSHService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by alex on 2016/10/11.
 */
@Slf4j
public class SystemWebSocketHandler implements WebSocketHandler {

  private static final LinkedList<WebSocketSession> sessions = Lists.newLinkedList();
  private static final HashMap<String, WebSocketSession> sessionHashMap = Maps.newHashMap();

  @Autowired
  private SSHService sshService;

  @Autowired
  private MachineService machineService;

  private Machine machine;
  private ApplicationKey applicationKey;
  private JSch jsch;
  private Session session;
  private ChannelShell channel;
  private InputStream outFromChannel;
  private OutputStream inputToChannel;
  private PrintStream shellStream;

  public SystemWebSocketHandler() {}

  @Override
  public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
    log.info("after connection established.");
  }

  @Override
  public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
    String key = (String) webSocketMessage.getPayload();
    log.info("handle message. [msg] : " + key);

    // 消息发送给后端fork的socket
    // webSocketSession.sendMessage(webSocketMessage);

  }

  @Override
  public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
    log.info("handle transport error.");
//    if (webSocketSession.isOpen()) {
//      webSocketSession.close();
//    }
//    sessions.remove(webSocketSession);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
    log.info("after connection closed.");
    //sessions.remove(webSocketSession);




    if (channel instanceof ChannelShell && channel.isConnected()) {
      channel.disconnect();
    }

    if (session instanceof Session && session.isConnected()) {
      session.disconnect();
    }
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
