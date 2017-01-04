package com.fxiaoke.jason.mapper;

import com.fxiaoke.jason.entity.ApplicationKey;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by alex on 2016/10/9.
 */
public interface ApplicationKeyMapper extends ICrudMapper<ApplicationKey> {
  @Select("SELECT * FROM tbl_application_key WHERE host=#{host} LIMIT 0,1")
  ApplicationKey findByHost(@Param("host") String host);
}
