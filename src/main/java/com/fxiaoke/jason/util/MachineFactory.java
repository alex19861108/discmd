package com.fxiaoke.jason.util;

import com.fxiaoke.common.PasswordUtil;
import com.github.autoconf.ConfigFactory;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alex on 2016/9/30.
 */
public class MachineFactory {
  private static Logger log = LoggerFactory.getLogger(MachineFactory.class);

  private static final MachineFactory instance = new MachineFactory();

  private String defaultUser;
  private Map<String, String> user2PwdMap = Maps.newHashMap();
  private Map<String, String> ip2UserMap = Maps.newHashMap();

  private Map<String, String> ip2UserMapFuzzy = Maps.newHashMap();

  private MachineFactory() {
    ConfigFactory.getConfig("publish-server", (config) -> {

      Pattern userPwdPattern = Pattern.compile("user\\.([\\w\\d.@]+)\\s*=\\s*([a-fA-F0-9]+)\\s*");
      Pattern userIpPattern = Pattern.compile("([\\w\\d.@]+)\\.ips\\s*=\\s*(.+)\\s*");

      CharMatcher separator = CharMatcher.anyOf(", ;|");
      Splitter splitter = Splitter.on(separator).trimResults().omitEmptyStrings();
      for (String line : config.getLines()) {

        Matcher matcher = userPwdPattern.matcher(line);
        if (matcher.matches()) {
          try {

            user2PwdMap.put(matcher.group(1), PasswordUtil.decode(matcher.group(2)));

          } catch (Exception e) {
            log.error("cannot decode: " + matcher.group(2), e);
          }
        } else {

          matcher = userIpPattern.matcher(line);
          if (matcher.matches()) {
            for (String ip : splitter.split(matcher.group(2))) {
              if (fullHost(ip)) {
                ip2UserMap.put(ip, matcher.group(1));
              } else {
                ip2UserMapFuzzy.put(ip, matcher.group(1));
              }
            }
          }
        }
      }

      defaultUser = config.get("default.user");

      log.info("users:{}, special:{}", user2PwdMap.keySet(), ip2UserMap);
    });
  }

  public static MachineFactory getInstance() {
    return instance;
  }

  public Map<String, String> getUser2PwdMap() {
    return user2PwdMap;
  }

  public Map<String, String> getIp2UserMap() {
    return ip2UserMap;
  }

  public Map<String, String> getIp2UserMapFuzzy() {
    return ip2UserMapFuzzy;
  }

  public String getDefaultUser() {
    return defaultUser;
  }

  /**
   * 根据ip获取密码
   * @param host
   * @return
   */
  public String getPasswordByIp(String host) {
    String user = getUserByIp(host);

    if (!Strings.isNullOrEmpty(user)) {
      Map<String, String> user2PwdMap = getUser2PwdMap();
      String password = user2PwdMap.getOrDefault(user, null);
      return password;
    }

    return null;
  }

  /**
   * 根据ip获取用户名
   * @param host
   * @return
   */
  public String getUserByIp(String host) {

    // 精确匹配
    Map<String, String> ip2UserMap = getIp2UserMap();
    String user = ip2UserMap.get(host);

    if (Strings.isNullOrEmpty(user)) {
      // 模糊匹配
      Map<String, String> ip2UserMapFuzzy = getIp2UserMapFuzzy();
      for (Map.Entry<String, String> entry : ip2UserMapFuzzy.entrySet()) {
        String ipFuzzy = entry.getKey();
        Pattern ptn = Pattern.compile(ipFuzzy + ".*");
        if (ptn.matcher(host).matches()) {
          user = ip2UserMapFuzzy.get(ipFuzzy);
          break;
        }
      }
    }

    if (Strings.isNullOrEmpty(user)) {
      return defaultUser;
    } else {
      return user;
    }
  }

  /**
   * 验证host是否是有效host
   * @param host
   * @return
   */
  public boolean fullHost(String host) {
    Pattern ptn = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    return ptn.matcher(host).matches();
  }
}

