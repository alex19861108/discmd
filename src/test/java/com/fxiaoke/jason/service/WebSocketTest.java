package com.fxiaoke.jason.service;

import org.junit.Test;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2016/10/12.
 */
public class WebSocketTest {

  @ClientEndpoint
  public class Client {

    @OnOpen
    public void onOpen(Session session) {
      System.out.println("Connected to endpoint: " + session.getBasicRemote());
    }

    @OnMessage
    public void onMessage(String message) {
      System.out.println(message);
    }

    @OnError
    public void onError(Throwable t) {
      t.printStackTrace();
    }
  }

  @Test
  public void testTest() {
    List<Transport> transports = new ArrayList<>(2);
    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
    transports.add(new RestTemplateXhrTransport());

    SockJsClient sockJsClient = new SockJsClient(transports);
    //sockJsClient.doHandshake(new MyWebSocketHander(), "ws://example.com:8080/sockjs");
  }

  @Test
  public void testWebSocket() {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    String uri = "ws://localhost:8080/discmd/terminal/webSocket";
    try {
      Session session = container.connectToServer(Client.class, new URI(uri));
      char lf = 10; // 这个是换行
      char nl = 0; // 这个是消息结尾的标记，一定要
      StringBuilder sb = new StringBuilder();
      sb.append("SEND").append(lf); // 请求的命令策略
      sb.append("destination:/app/terminal/hello").append(lf); // 请求的资源
      sb.append("content-length:14").append(lf).append(lf); // 消息体的长度
      sb.append("{\"name\":\"123\"}").append(nl); // 消息体
      session.getBasicRemote().sendText(sb.toString()); // 发送消息
      Thread.sleep(50000); // 等待一小会
      session.close();
    } catch (DeploymentException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }


  }
}
