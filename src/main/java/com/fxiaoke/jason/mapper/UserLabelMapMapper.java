package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.Label;
import com.fxiaoke.jason.entity.UserLabelMap;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by alex on 16/9/28.
 */
public interface UserLabelMapMapper extends ICrudMapper<UserLabelMap> {
  @Select("SELECT * FROM `tbl_user_label_map` WHERE user_id=#{userId} and label_id=#{labelId} LIMIT 0,1")
  UserLabelMap findByUserAndLabel(@Param("userId") Long userId, @Param("labelId") Long labelId);

  @Select("SELECT * FROM tbl_user_label_map WHERE label_id=#{labelId}")
  List<UserLabelMap> findByLabel(@Param("labelId") Long labelId);

  @Select("SELECT tbl_label.* FROM tbl_label left join tbl_user_label_map on tbl_label.id=tbl_user_label_map.label_id WHERE tbl_user_label_map.user_id=#{userId}")
  List<Label> findLabelsForUser(@Param("userId") Long userId);

  @Delete("DELETE FROM tbl_user_label_map WHERE user_id=#{userId} and label_id=#{labelId}")
  int deleteByUserAndLabel(@Param("userId") Long userId, @Param("labelId") Long labelId);
}
