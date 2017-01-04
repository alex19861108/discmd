package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;
import java.util.Date;

/**
 * Created by alex on 2016/10/11.
 */
@Table(name = "tbl_session_log")
@Getter
@Setter
@NoArgsConstructor
public class SessionLog {
  private long id;
  private String userName;
  private String sessionId;
  private String statusCd;
  private String errorMsg;
  private Date beginDt;
  private Date endDt;

  public static String RUNNING = "RUNNING";
  public static String CLOSED = "CLOSED";
  public static String ERROR = "ERROR";
}
