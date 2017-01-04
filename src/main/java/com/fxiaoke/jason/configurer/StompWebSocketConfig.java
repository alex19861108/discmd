package com.fxiaoke.jason.configurer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Created by alex on 2016/10/12.
 *
 * 启用STOMP协议WebSocket配置
 */
@Configuration
@EnableWebMvc
@EnableWebSocketMessageBroker   // 能够在 WebSocket 上启用 STOMP
@Slf4j
public class StompWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer{

  /**
   * 这个方法的作用是添加一个服务端点，来接收客户端的连接
   * @param stompEndpointRegistry
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
    // 表示添加了一个端点，客户端就可以通过这个端点来进行连接。
    // withSockJS()的作用是开启SockJS支持
    stompEndpointRegistry.addEndpoint("/ws").withSockJS();
    log.error("registerStompEndpoints...");
  }

  /**
   * 这个方法的作用是定义消息代理，通俗一点讲就是设置消息连接请求的各种规范信息。
   * @param messageBrokerRegistry
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {

    /**
     * 下面两个方法定义的信息其实是相反的，一个定义了客户端接收的地址前缀，一个定义了客户端发送地址的前缀
     */
    // 表示客户端订阅地址的前缀信息，也就是客户端接收服务端消息的地址的前缀信息
    messageBrokerRegistry.enableSimpleBroker("/topic");
    //messageBrokerRegistry.enableStompBrokerRelay("/response");

    // 指服务端接收地址的前缀，意思就是说客户端给服务端发消息的地址的前缀, 将/app开头的地址标识为应用地址
    messageBrokerRegistry.setApplicationDestinationPrefixes("/app");

    log.error("configureMessageBroker finished.");

  }
}
