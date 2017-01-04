package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.ApplicationKey;
import com.fxiaoke.jason.mapper.ApplicationKeyMapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by alex on 2016/10/9.
 */
@Service
@Slf4j
public class ApplicationKeyService {

  private static final String CACHE_VALUE = "ApplicationKey";

  @Autowired
  private ApplicationKeyMapper mapper;

  public ApplicationKey findByHost(String host) { return mapper.findByHost(host); }

  public int insert(ApplicationKey applicationKey) { return mapper.insert(applicationKey); }

  public int update(ApplicationKey applicationKey) { return mapper.update(applicationKey); }

  @Cacheable(value = CACHE_VALUE)
  public ApplicationKey findKey() {
    String hostAddress = null;
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      log.error("get local host failed.", e);
      return null;
    }

    if (Strings.isNullOrEmpty(hostAddress)) return null;

    ApplicationKey applicationKey = findByHost(hostAddress);

    return applicationKey;
  }
}
