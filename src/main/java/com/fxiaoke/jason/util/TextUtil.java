package com.fxiaoke.jason.util;

import com.google.common.base.Splitter;

import java.util.List;

/**
 * Created by alex on 2016/10/10.
 */
public class TextUtil {
  private TextUtil() {}

  public static List<String> lineFeedToList(String strs) {
    List<String> strList = Splitter.on("\r\n").omitEmptyStrings().trimResults().splitToList(strs);
    return strList;
  }
}
