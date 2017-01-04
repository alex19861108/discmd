package com.fxiaoke.jason.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fxiaoke.jason.util.HttpSessionContainerFactory.log;

/**
 * Created by alex on 2016/10/25.
 */
public class LabelWebSocketHandlerTest {
  //private String cookie = "JSESSIONID=16F8A9C8F8024ADC74F2CF4762539801; Webstorm-23f422e2=429ac040-2e43-447a-96af-4b5845f5b783";
  private String cookie = "JSESSIONID=16F8A9C8F8024ADC74F2CF4762539801";

  @Test
  public void testPtn() {
    String sessionId = null;
    String reg = "JSESSIONID=(\\w*)";
    Pattern ptn = Pattern.compile(reg);
    Matcher matcher = ptn.matcher(cookie);
    if (matcher.find()) {
      sessionId = matcher.group(1);
      log.info(sessionId);
    }
    Assert.assertEquals(sessionId, "16F8A9C8F8024ADC74F2CF4762539801");
  }
}
