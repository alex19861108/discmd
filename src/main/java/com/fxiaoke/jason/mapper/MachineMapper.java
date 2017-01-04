package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.Machine;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by alex on 16/9/26.
 */
public interface MachineMapper extends ICrudMapper<Machine> {

  @Select("SELECT * FROM `tbl_machine` where host like '%${host}%' ORDER BY id ${order} limit #{offset},#{rowCnt} ")
  List<Machine> findLimitByHost(@Param("host") String host, @Param("offset") int offset, @Param("rowCnt") int rowCnt, @Param("order") String order);

  @Select("SELECT * FROM `tbl_machine` ORDER BY id ${order} limit #{offset},#{rowCnt}")
  List<Machine> findLimit(@Param("offset") int offset, @Param("rowCnt") int rowCnt, @Param("order") String order);

  @Select("SELECT * FROM `tbl_machine` where host=#{host} ORDER BY id DESC LIMIT 0,1")
  Machine findByHost(@Param("host") String host);

  @Select("SELECT COUNT(*) FROM `tbl_machine`")
  int findCount();

  @Select("SELECT * FROM `tbl_machine` WHERE status_cd != 'success' ")
  List<Machine> findLostConnectionMachines();

  @Select("SELECT * FROM `tbl_machine` WHERE id in (${ids})")
  List<Machine> findByIds(@Param("ids") String ids);

  @Select("SELECT tbl_machine.*, tbl_label.id as label_id, tbl_label.nm as label_nm, tbl_label.desc as label_desc FROM "
    + "`tbl_machine` left join `tbl_machine_label_map` on tbl_machine.id=tbl_machine_label_map.machine_id left join "
    + "tbl_label on tbl_machine_label_map.label_id=tbl_label.id ORDER BY tbl_machine.id  ${order} limit #{offset},#{rowCnt}")
  List<Machine> findLimitJoinLabel(@Param("offset") int offset, @Param("rowCnt") int rowCnt, @Param("order") String order);

  @Select("SELECT tbl_machine.*, tbl_label.id as label_id, tbl_label.nm as label_nm, tbl_label.desc as label_desc FROM "
    + "`tbl_machine` left join `tbl_machine_label_map` on tbl_machine.id=tbl_machine_label_map.machine_id left join "
    + "tbl_label on tbl_machine_label_map.label_id=tbl_label.id WHERE tbl_machine.host like '%${fuzzy}%' or tbl_label.nm like '%${fuzzy}%' "
    + "ORDER BY tbl_machine.id  ${order} limit #{offset},#{rowCnt}")
  List<Machine> findLimitFuzzyJoinLabel(@Param("fuzzy") String fuzzy, @Param("offset") int offset, @Param("rowCnt") int rowCnt, @Param("order") String order);
}
