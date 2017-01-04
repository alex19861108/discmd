package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;

/**
 * Created by alex on 2016/10/10.
 */
@Table(name = "tbl_machine_status")
@Getter
@Setter
@NoArgsConstructor
public class MachineStatus {
  private long machineId;
  private long userId;
  private String statusCd;
}
