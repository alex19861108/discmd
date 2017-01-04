package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.Task;
import com.fxiaoke.jason.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by alex on 2016/11/18.
 */
@Service
public class TaskService {
  @Autowired
  private TaskMapper mapper;

  public int update(Task task) { return mapper.update(task); }

  public Task findById(Long id) { return mapper.findById(id); }

  public int insertAndSetObjectId(Task task) { return mapper.insertAndSetObjectId(task); }
}
