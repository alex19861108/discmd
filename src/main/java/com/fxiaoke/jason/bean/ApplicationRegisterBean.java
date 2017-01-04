package com.fxiaoke.jason.bean;

import com.fxiaoke.jason.entity.ApplicationKey;
import com.fxiaoke.jason.service.ApplicationKeyService;
import com.fxiaoke.jason.service.SSHService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by alex on 2016/10/9.
 */
@Slf4j
public class ApplicationRegisterBean {
  @Autowired
  private ApplicationKeyService applicationKeyService;

  @Autowired
  private SSHService sshService;

  public void init() {
    //genApplicationKey();
  }

  public void genApplicationKey() {
    try {
      String hostAddress = InetAddress.getLocalHost().getHostAddress();

      String passphrase = sshService.keyGen();
      String privateKey = sshService.getPrivateKey();
      String publicKey = sshService.getPublicKey();

      ApplicationKey applicationKey = applicationKeyService.findByHost(hostAddress);
      if (applicationKey == null) {
        applicationKey = new ApplicationKey();
        applicationKey.setHost(hostAddress);
        applicationKey.setPrivateKey(privateKey);
        applicationKey.setPublicKey(publicKey);
        applicationKey.setPassphrase(passphrase);

        applicationKeyService.insert(applicationKey);
      } else {
        applicationKey.setPublicKey(hostAddress);
        applicationKey.setPrivateKey(privateKey);
        applicationKey.setPublicKey(publicKey);
        applicationKey.setPassphrase(passphrase);

        applicationKeyService.update(applicationKey);
      }

    } catch (UnknownHostException e) {
      log.error("get local host failed.", e);
    }
  }
}
