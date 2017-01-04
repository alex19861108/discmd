package com.fxiaoke.jason.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by alex on 2016/10/13.
 */
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

    if (request instanceof ServletServerHttpRequest) {
      ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest)request;
      HttpServletRequest httpServletRequest = servletServerHttpRequest.getServletRequest();
      attributes.put("machineId", httpServletRequest.getParameter("machineId"));
    }

    return super.beforeHandshake(request, response, wsHandler, attributes);
  }
}
