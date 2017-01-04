package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.SessionLog;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by alex on 2016/10/11.
 */
public interface SessionLogMapper extends ICrudMapper<SessionLog> {
  @Select("SELECT * FROM tbl_session_log WHERE session_id=#{sessionId} ORDER BY id DESC LIMIT 0, 1")
  SessionLog findBySession(@Param("sessionId") String sessionId);
}
