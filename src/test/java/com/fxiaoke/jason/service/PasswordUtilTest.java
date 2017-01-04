package com.fxiaoke.jason.service;

import com.fxiaoke.common.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by alex on 2016/11/11.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class PasswordUtilTest {

  @Test
  public void testEncode() {
    String raw = "pp00--[[";
    try {
      String pwd = PasswordUtil.encode(raw);
      log.error(pwd);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testDecode() {
    //String pwd = "C2D9D3544279FBA585566BDE056F2E37746D0896E6265F1B";
    String pwd = "8B523A58A439F9FD3C5FA89D283A0F9CA8B9FB45D7663D641340A4EDB838106B";
    try {
      String raw = PasswordUtil.decode(pwd);
      log.error(raw);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
