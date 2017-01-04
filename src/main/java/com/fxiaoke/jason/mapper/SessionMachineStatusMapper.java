package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.SessionMachineStatus;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by alex on 2016/11/22.
 */
public interface SessionMachineStatusMapper extends ICrudMapper<SessionMachineStatus> {
  @Select("SELECT * FROM tbl_session_machine_status WHERE session_id=#{sessionId} and machine_id=#{machineId} LIMIT 0, 1")
  SessionMachineStatus findBySessionAndMachine(@Param("sessionId") Long sessionId, @Param("machineId") Long machineId);

  @Select("SELECT * FROM tbl_session_machine_status WHERE session_id=#{sessionId}")
  List<SessionMachineStatus> findBySession(@Param("sessionId") String sessionId);
}
