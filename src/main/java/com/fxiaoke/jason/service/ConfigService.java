package com.fxiaoke.jason.service;

import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Splitter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alex on 2016/10/28.
 */
@Service
public class ConfigService {
  @ReloadableProperty("casServerUrlPrefix")
  private String casServerUrlPrefix = "/cas";

  @ReloadableProperty("SuperManager")
  private String superManagerStr = "";

  @ReloadableProperty("tmpUploadDir")
  private String tmpUploadDir = "/tmp/upload";

  public String getCasServerUrlPrefix() {
    return casServerUrlPrefix;
  }

  public List<String> getSuperManagers() {
    List<String> superManagers = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(this.superManagerStr);
    return superManagers;
  }

  /**
   * 上传文件的临时存储目录
   * @return
   */
  public String getTmpUploadDir() {
    return tmpUploadDir;
  }
}
