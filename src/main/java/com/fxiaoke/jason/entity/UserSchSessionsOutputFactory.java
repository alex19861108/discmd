package com.fxiaoke.jason.entity;

/**
 * Created by alex on 2016/10/25.
 */
public class UserSchSessionsOutputFactory {
  private static UserSchSessionsOutput instance = new UserSchSessionsOutput();
  private UserSchSessionsOutputFactory() {
  }

  public static UserSchSessionsOutput getInstance() {
    return instance;
  }
}
