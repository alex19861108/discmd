package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by alex on 2016/10/28.
 */
@Getter
@Setter
public class MachineLabelUnion {
  private Machine machine;
  private List<Label> labels;
}
