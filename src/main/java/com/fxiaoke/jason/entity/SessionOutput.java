package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * session在machine上的输出
 * Created by alex on 2016/10/11.
 */
@Getter
@Setter
@NoArgsConstructor
public class SessionOutput extends Machine {
  private String sessionId;
  private StringBuilder output = new StringBuilder();

  public SessionOutput(String sessionId, Machine machine) {
    this.sessionId = sessionId;
    this.setId(machine.getId());
    this.setInstanceId(machine.getInstanceId());
    this.setHost(machine.getHost());
  }
}
