package com.fxiaoke.jason.entity;

/**
 * Created by alex on 2016/10/25.
 */
public class UserSchSessionsFactory {
  private static UserSchSessions instance = new UserSchSessions();
  private UserSchSessionsFactory() {
  }

  public static UserSchSessions getInstance() {
    return instance;
  }
}
