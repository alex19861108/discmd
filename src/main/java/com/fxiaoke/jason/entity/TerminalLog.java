package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;
import java.util.Date;

/**
 * Created by alex on 2016/10/11.
 */
@Table(name = "tbl_terminal_log")
@Getter
@Setter
@NoArgsConstructor
public class TerminalLog {
  private long sessionId;
  private long instanceId;
  private long machineId;
  private String output;
  private Date logTm;
}
