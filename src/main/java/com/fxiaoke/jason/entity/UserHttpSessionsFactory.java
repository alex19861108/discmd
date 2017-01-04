package com.fxiaoke.jason.entity;

/**
 * Created by alex on 2016/10/25.
 */
public class UserHttpSessionsFactory {
  private static UserHttpSessions instance = new UserHttpSessions();
  private UserHttpSessionsFactory() {
  }

  public static UserHttpSessions getInstance() {
    return instance;
  }
}
