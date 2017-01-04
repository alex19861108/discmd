package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;

/**
 * Created by alex on 16/9/26.
 */
@Table(name = "tbl_machine_user_map")
@Setter
@Getter
@NoArgsConstructor
public class MachineUserMap {
  private Long id;
  private String user;
  private String password;
  private String statusCd = Machine.INITIAL_STATUS;
  private String machineId;
}