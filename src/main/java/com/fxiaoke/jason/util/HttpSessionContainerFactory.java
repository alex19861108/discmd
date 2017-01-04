package com.fxiaoke.jason.util;


import com.fxiaoke.jason.entity.TerminalConnector;
import com.google.common.collect.Maps;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by alex on 2016/10/21.
 */
public class HttpSessionContainerFactory {
  public static final Logger log = LoggerFactory.getLogger(HttpSessionContainerFactory.class);

  private static HttpSessionContainer instance = new HttpSessionContainer();

  private HttpSessionContainerFactory() {}

  public static HttpSessionContainer getInstance() { return instance; }


  @NoArgsConstructor
  public static class HttpSessionContainer {
    private HashMap<String, TerminalConnector> map = Maps.newHashMap();

    public Object put(String key, TerminalConnector value) {
      return map.put(key, value);
    }

    public TerminalConnector get(String key) {
      return map.get(key);
    }
  }
}
