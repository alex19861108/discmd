package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.Job;
import com.fxiaoke.jason.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by alex on 2016/11/18.
 */
@Service
public class JobService {
  @Autowired
  private JobMapper mapper;

  public Job findById(Long id) { return mapper.findById(id);}

  public int update(Job job) { return mapper.update(job); }

  public int insertAndSetObjectId(Job job) { return  mapper.insertAndSetObjectId(job); }
}
