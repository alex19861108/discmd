package com.fxiaoke.jason.entity;

import com.fxiaoke.jason.service.SSHService;
import com.github.mybatis.entity.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Table;
import java.util.Date;

/**
 * Created by alex on 2016/11/18.
 */
@Table(name = "tbl_task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends IdEntity {
  private Long id;
  private String nm;
  private String taskType;
  private String desc;
  private String statusCd;
  private Date beginDt;
  private Date endDt;

  @Autowired
  private SSHService sshService;

  public static final String PENDING = "PENDING";
  public static final String RUNNING = "RUNNING";
  public static final String SUCCESS = "SUCCESS";
  public static final String FAILED = "FAILED";
}
