package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.Label;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by alex on 16/9/28.
 */
public interface LabelMapper extends ICrudMapper<Label> {
  @Select("SELECT * FROM tbl_label WHERE `nm` like '%${nm}%' ORDER BY id ${order} limit #{offset}, #{rowCnt}")
  List<Label> findLimitByName(@Param("nm") String nm, @Param("offset") int offset, @Param("rowCnt") int rowCnt, @Param("order") String order);

  @Select("SELECT * FROM tbl_label ORDER BY id ${order} limit #{offset}, #{rowCnt}")
  List<Label> findLimit(@Param("offset") int offset, @Param("rowCnt") int rowCnt, @Param("order") String order);

  @Select("SELECT COUNT(*) FROM tbl_label")
  int findCount();

  @Select("SELECT * FROM tbl_label WHERE nm=#{name} ORDER BY id DESC LIMIT 0,1")
  Label findByName(@Param("name") String name);

  @Delete("DELETE FROM tbl_label WHERE nm=#{name}")
  int deleteByName(@Param("name") String name);
}
