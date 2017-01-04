package com.fxiaoke.jason.entity;

import com.github.mybatis.entity.IdEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;

/**
 * Created by alex on 16/9/28.
 */
@Table(name = "tbl_user_label_map")
@Setter
@Getter
@NoArgsConstructor
public class UserLabelMap extends IdEntity {
  private long userId;
  private long labelId;
}
