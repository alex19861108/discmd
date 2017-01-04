package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;

/**
 * Created by alex on 2016/10/9.
 */
@Table(name = "tbl_application_key")
@NoArgsConstructor
@Getter
@Setter
public class ApplicationKey {
  private long id;
  private String host;
  private String publicKey;
  private String privateKey;
  private String passphrase;
}
