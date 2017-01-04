package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.JobTaskMap;
import com.fxiaoke.jason.entity.Task;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by alex on 2016/11/18.
 */
public interface JobTaskMapMapper extends ICrudMapper<JobTaskMap> {
  @Select("SELECT tbl_task.* FROM tbl_job_task_map LEFT JOIN tbl_task ON tbl_job_task_map.task_id = tbl_task.id WHERE tbl_job_task_map.job_id=#{jobId} and tbl_task.status!=\"SUCCESS\"")
  List<Task> findNotSuccTaskForJob(@Param("jobId") Long jobId);

  @Select("SELECT tbl_task.* FROM tbl_job_task_map LEFT JOIN tbl_task ON tbl_job_task_map.task_id = tbl_task.id WHERE tbl_job_task_map.job_id=#{jobId}")
  List<Task> findTasksForJob(@Param("jobId") Long jobId);
}
