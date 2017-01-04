package com.fxiaoke.jason.entity;

import com.github.mybatis.entity.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;

/**
 * Created by alex on 2016/11/22.
 */
@Table(name = "tbl_session_machine_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionMachineStatus extends IdEntity{
  private Long id;
  private String sessionId;
  private Long machineId;
  private Integer instanceId;
  private String statusCd = Machine.INITIAL_STATUS;
  private String errorMsg;
}
