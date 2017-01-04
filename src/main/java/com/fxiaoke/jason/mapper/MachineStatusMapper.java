package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.MachineStatus;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by alex on 2016/10/10.
 */
public interface MachineStatusMapper extends ICrudMapper<MachineStatus> {
  @Select("SELECT * FROM tbl_machine_status WHERE machine_id=#{machineId} and user_id=#{userId} LIMIT 0,1")
  MachineStatus findByMachineIdAndUserId(@Param("machineId") Long machineId, @Param("userId") long userId);

  @Update("UPDATE tbl_machine_status SET status_cd=#{statusCd} WHERE machine_id=#{machineId} and user_id=#{userId}")
  int updateStatusByMachineIdAndUserId(@Param("machineId") Long machineId, @Param("userId") Long userId, @Param("statusCd") String statusCd);
}
