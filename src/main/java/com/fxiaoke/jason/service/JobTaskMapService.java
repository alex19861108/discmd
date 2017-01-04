package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.JobTaskMap;
import com.fxiaoke.jason.entity.Task;
import com.fxiaoke.jason.mapper.JobTaskMapMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alex on 2016/11/18.
 */
@Service
public class JobTaskMapService {
  @Autowired
  private JobTaskMapMapper mapper;

  public int insertAndSetObjectId(JobTaskMap jobTaskMap) { return mapper.insertAndSetObjectId(jobTaskMap); }

  public int insert(JobTaskMap jobTaskMap) { return mapper.insert(jobTaskMap); }

  public List<Task> findNotSuccessTaskForJob(Long jobId) {
    return mapper.findNotSuccTaskForJob(jobId);
  }

  public List<Task> findTasksForJob(Long jobId) {
    return mapper.findTasksForJob(jobId);
  }
}
