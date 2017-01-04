package com.fxiaoke.jason.entity;

import com.github.mybatis.entity.IdEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;
import java.util.Date;

/**
 * Created by alex on 2016/11/9.
 */
@Table(name = "tbl_command_record")
@Getter
@Setter
@NoArgsConstructor
public class CommandRecord extends IdEntity {
  private Long id;
  private String userName;
  private String command;
  private Date recordDt;
}
