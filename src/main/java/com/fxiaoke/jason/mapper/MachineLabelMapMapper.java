package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.Label;
import com.fxiaoke.jason.entity.Machine;
import com.fxiaoke.jason.entity.MachineLabelMap;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by alex on 16/9/28.
 */
public interface MachineLabelMapMapper extends ICrudMapper<MachineLabelMap> {
  @Select("SELECT * FROM `tbl_machine_label_map` WHERE machine_id=#{machineId} and label_id=#{labelId} LIMIT 0,1")
  MachineLabelMap findByMachineAndLabel(@Param("machineId") Long machineId, @Param("labelId") Long labelId);

  @Select("SELECT * FROM tbl_machine WHERE id in (SELECT machine_id FROM tbl_machine_label_map WHERE label_id=#{labelId})")
  List<Machine> findMachinesForLabel(@Param("labelId") Long labelId);

  @Select("SELECT * FROM tbl_label WHERE id in (SELECT label_id FROM tbl_machine_label_map WHERE machine_id=#{machineId})")
  List<Label> findLabelsForMachine(@Param("machineId") Long machineId);

  @Delete("DELETE FROM tbl_machine_label_map WHERE machine_id=#{machineId} and label_id=#{labelId}")
  int deleteByMachineAndLabel(@Param("machineId") Long machineId,@Param("labelId") Long labelId);

  @Select("SELECT * FROM tbl_machine WHERE id in (SELECT DISTINCT machine_id FROM tbl_machine_label_map WHERE label_id in (${labelIds}))")
  List<Machine> findMachinesForLabels(@Param("labelIds") String labelIds);

  @Select("SELECT * FROM tbl_label WHERE id in (SELECT tbl_machine_label_map.label_id FROM tbl_machine_label_map INNER JOIN tbl_user_label_map ON tbl_machine_label_map.label_id = tbl_user_label_map.label_id WHERE tbl_machine_label_map.machine_id=#{machineId} and tbl_user_label_map.user_id=#{userId})")
  List<Label> findUserOwnedLabelsForMachine(@Param("machineId") Long machineId, @Param("userId") Long userId);
}
