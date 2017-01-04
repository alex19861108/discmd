package com.fxiaoke.jason.entity;

import com.github.mybatis.entity.IdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by alex on 16/9/26.
 */
@Table(name = "tbl_machine")
@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Machine extends IdEntity {
  private Long id;
  private String host;
  @Transient
  private Integer port = 22;
  @Transient
  private String statusCd;
  @Transient
  private String errorMsg;
  @Transient
  private Integer instanceId;

  public static final String INITIAL_STATUS="INITIAL";
  public static final String AUTH_FAIL_STATUS="AUTHFAIL";
  public static final String PUBLIC_KEY_FAIL_STATUS="KEYAUTHFAIL";
  public static final String GENERIC_FAIL_STATUS="GENERICFAIL";
  public static final String SUCCESS_STATUS="SUCCESS";
  public static final String HOST_FAIL_STATUS="HOSTFAIL";
}