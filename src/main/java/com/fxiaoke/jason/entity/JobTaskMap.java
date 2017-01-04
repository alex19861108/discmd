package com.fxiaoke.jason.entity;

import com.github.mybatis.entity.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;

/**
 * Created by alex on 2016/11/18.
 */
@Table(name = "tbl_job_task_map")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobTaskMap extends IdEntity {
  private Long id;
  private Long jobId;
  private Long taskId;
}
