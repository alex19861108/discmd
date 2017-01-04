package com.fxiaoke.jason.entity;

/**
 * Created by alex on 2016/10/25.
 */
public class UserHttpSessionsOutputFactory {
  private static UserHttpSessionsOutput instance = new UserHttpSessionsOutput();
  private UserHttpSessionsOutputFactory() {}
  public static UserHttpSessionsOutput getInstance() { return instance; }
}
