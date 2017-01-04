package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.CommandRecord;
import com.fxiaoke.jason.mapper.CommandRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by alex on 2016/11/9.
 */
@Service
public class CommandRecordService {
  @Autowired
  private CommandRecordMapper mapper;

  @Async
  public int insert(CommandRecord commandRecord) { return mapper.insert(commandRecord); }
}
