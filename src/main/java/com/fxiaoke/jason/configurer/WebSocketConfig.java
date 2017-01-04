package com.fxiaoke.jason.configurer;

import com.fxiaoke.jason.handler.LabelWebSocketHandler;
import com.fxiaoke.jason.handler.SystemTextWebSocketHandler;
import com.fxiaoke.jason.handler.SystemWebSocketHandler;
import com.fxiaoke.jason.interceptor.WebSocketHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by alex on 2016/10/11.
 *
 * 注册普通WebScoket
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
    webSocketHandlerRegistry.addHandler(systemWebSocketHandler(), "/terminal/webSocket").addInterceptors(webSocketHandshakeInterceptor()).withSockJS();
    webSocketHandlerRegistry.addHandler(systemTextWebSocketHandler(), "/terminal/textWebSocket").withSockJS();
    webSocketHandlerRegistry.addHandler(labelWebSocketHandler(), "/terminal/label/webSocket").withSockJS();
  }

  @Bean
  public WebSocketHandler labelWebSocketHandler() { return new LabelWebSocketHandler(); }

  @Bean
  public WebSocketHandler systemWebSocketHandler() {
    return new SystemWebSocketHandler();
  }

  @Bean
  public TextWebSocketHandler systemTextWebSocketHandler() {
    return new SystemTextWebSocketHandler();
  }

  @Bean
  public WebSocketHandshakeInterceptor webSocketHandshakeInterceptor() {
    return new WebSocketHandshakeInterceptor();
  }

  @Bean
  public WebSocketContainerFactoryBean createWebSocketContainer() {
    WebSocketContainerFactoryBean container = new WebSocketContainerFactoryBean();
    container.setMaxTextMessageBufferSize(8192);
    container.setMaxBinaryMessageBufferSize(8192);
    container.setMaxSessionIdleTimeout(0);
    return container;
  }
}
