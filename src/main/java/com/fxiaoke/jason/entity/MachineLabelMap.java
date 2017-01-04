package com.fxiaoke.jason.entity;

import com.github.mybatis.entity.IdEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

/**
 * Created by alex on 16/9/28.
 */
@Table(name = "tbl_machine_label_map")
@Getter
@Setter
public class MachineLabelMap extends IdEntity {
  private long machineId;
  private long labelId;
}
