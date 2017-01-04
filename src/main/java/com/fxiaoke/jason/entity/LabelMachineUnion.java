package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by alex on 2016/11/10.
 */
@Getter
@Setter
public class LabelMachineUnion {
  private Label label;
  private List<Machine> machines;
}
