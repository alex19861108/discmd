package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.TerminalLog;
import com.fxiaoke.jason.mapper.TerminalLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by alex on 2016/10/11.
 */
@Service
public class TerminalLogService {
  @Autowired
  private TerminalLogMapper mapper;

  public int insert(TerminalLog terminalLog) {
    return mapper.insert(terminalLog);
  }
}
