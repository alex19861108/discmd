package com.fxiaoke.jason.task;

import com.fxiaoke.jason.entity.SessionOutput;
import com.fxiaoke.jason.util.SessionOutputUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alex on 2016/10/11.
 */
@Slf4j
public class SecureShellTask implements Runnable {

  private InputStream outFromChannel;
  private SessionOutput sessionOutput;

  public SecureShellTask(SessionOutput sessionOutput, InputStream outFromChannel) {

    this.sessionOutput = sessionOutput;
    this.outFromChannel = outFromChannel;
  }

  @Override
  public void run() {
    // 读取channel的输出,保存到全局map中
    InputStreamReader isr = new InputStreamReader(outFromChannel);
    BufferedReader br = new BufferedReader(isr);
    try {

      SessionOutputUtil.addOutput(sessionOutput);

      char[] buff = new char[1024];
      int read;

      while((read = br.read(buff)) != -1) {

        SessionOutputUtil.addToOutput(sessionOutput.getSessionId(), sessionOutput.getInstanceId(), buff, 0, read);
        Thread.sleep(50);
      }

      SessionOutputUtil.removeOutput(sessionOutput.getSessionId(), sessionOutput.getInstanceId());


      /**
      while (true) {
        buff = new char[] {'t', 'e', '4'};
        SessionOutputUtil.addToOutput(sessionOutput.getSessionId(), sessionOutput.getInstanceId(), buff, 0, 3);
        Thread.sleep(1000);
      }
       */

    } catch (Exception ex) {
      log.error("run() error: {}", ex.toString(), ex);
    }
  }
}
