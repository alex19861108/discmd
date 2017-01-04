package com.fxiaoke.jason.entity;

import com.github.mybatis.entity.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;

/**
 * Created by alex on 16/9/28.
 */
@Table(name = "tbl_label")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Label extends IdEntity {
  private Long id;
  private String nm;
  private String desc;
}
