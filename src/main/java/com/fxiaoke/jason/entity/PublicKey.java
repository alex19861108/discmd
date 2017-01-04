package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Created by alex on 2016/10/10.
 */
@NoArgsConstructor
@Getter
@Setter
public class PublicKey {
  private long id;
  private String keyNm;
  private String type;
  private String fingerprint;
  private String publicKey;
  private boolean enabled;
  private Date createDt;
  private long userId;
  private long labelId;
}
