package com.fxiaoke.jason.configurer;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by alex on 2016/10/11.
 *
 * Configure web sockets and set the http session
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
  @Override
  public void modifyHandshake(ServerEndpointConfig config,
                              HandshakeRequest request,
                              HandshakeResponse response)
  {
    HttpSession httpSession = (HttpSession)request.getHttpSession();
    config.getUserProperties().put(HttpSession.class.getName(),httpSession);
  }
}