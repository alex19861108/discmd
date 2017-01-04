package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.*;
import com.fxiaoke.jason.task.SecureShellTask;
import com.fxiaoke.jason.util.EncryptionUtil;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by alex on 2016/10/9.
 */
@Slf4j
@Service
public class SSHService {

  private static final String PRIVATE_KEY = "privateKey";
  private static final String PUBLIC_KEY = "publicKey";

  //system path to public/private key
  //@ReloadableProperty("keyPath")
  //private String KEY_PATH = "~/.ssh";
  private static final String KEY_PATH = SSHService.class.getClassLoader().getResource("").getPath();

  //key type - rsa or dsa
  private static final String KEY_TYPE = "rsa";
  private static final int KEY_LENGTH = 2048;

  //private key name
  private static String PVT_KEY = KEY_PATH + "/id_" + KEY_TYPE;
  //public key name
  private static String PUB_KEY = PVT_KEY + ".pub";

  private static final int SERVER_ALIVE_INTERVAL = 60 * 1000;
  private static final int SESSION_TIMEOUT = 60000;
  private static final int CHANNEL_TIMEOUT = 60000;

  @ReloadableProperty("keyManagementEnabled")
  private boolean keyManagementEnabled = false;

  // private key 路径
  @ReloadableProperty(PRIVATE_KEY)
  private String privateKey = "";

  // public key 路径
  @ReloadableProperty(PUBLIC_KEY)
  private String publicKey = "";

  @ReloadableProperty("passphrase")
  private String passphrase = "";

  @ReloadableProperty("agentForwarding")
  private boolean agentForwarding = false;

  @Autowired
  private MachineService machineService;

  @Autowired
  private SessionMachineStatusService sessionMachineStatusService;

  /**
   * generates system's public/private key par and returns passphrase
   *
   * @return passphrase for system generated key
   */
  public String keyGen() {
    keyGen(passphrase);

    return passphrase;
  }

  /**
   * generates system's public/private key par and returns passphrase
   *
   * @return passphrase for system generated key
   */
  public String keyGen(String passphrase) {

    if (Strings.isNullOrEmpty(privateKey) || Strings.isNullOrEmpty(publicKey)) {

      //set key type
      int type = KeyPair.RSA;
      if("dsa".equals(SSHService.KEY_TYPE)) {
        type = KeyPair.DSA;
      } else if("ecdsa".equals(SSHService.KEY_TYPE)) {
        type = KeyPair.ECDSA;
      }
      String comment = "jason-statham@fxiaoke";

      JSch jsch = new JSch();

      try {

        KeyPair keyPair = KeyPair.genKeyPair(jsch, type, KEY_LENGTH);

        keyPair.writePrivateKey(PVT_KEY, passphrase.getBytes());
        keyPair.writePublicKey(PUB_KEY, comment);

        log.info("Finger print: " + keyPair.getFingerPrint());
        keyPair.dispose();
      } catch (Exception e) {
        log.error(e.toString(), e);
      }
    }

    return passphrase;
  }

  /**
   * returns the system's public key
   *
   * @return system's public key
   */
  public String getPublicKey() {

    String pubKey = PUB_KEY;
    //check to see if pub/pvt are defined in properties
    if (!Strings.isNullOrEmpty(privateKey) && !Strings.isNullOrEmpty(publicKey)) {
      pubKey = publicKey;
    }
    //read pvt ssh key
    File file = new File(pubKey);
    try {
      pubKey = FileUtils.readFileToString(file);
    } catch (Exception ex) {
      log.error("getPublicKey() error: {}", ex.toString(), ex);
    }

    return pubKey;
  }

  /**
   * returns the system's public key
   *
   * @return system's public key
   */
  public String getPrivateKey() {

    String pvtKey = PVT_KEY;
    //check to see if pub/pvt are defined in properties
    if (!Strings.isNullOrEmpty(privateKey) && !Strings.isNullOrEmpty(publicKey)) {
      pvtKey = publicKey;
    }

    //read pvt ssh key
    File file = new File(pvtKey);
    try {
      pvtKey = FileUtils.readFileToString(file);
    } catch (Exception ex) {
      log.error("getPrivateKey() error: {}", ex.toString(), ex);
    }

    return pvtKey;
  }

  /**
   * open new ssh session on host system
   *
   * @param userLoginAccount userLoginAccount
   * @param sessionId      session id
   * @param machine        host machine
   * @param userHttpSessions user session map
   * @return status of systems
   */
  public Machine openSSHTermOnMachine(Machine machine,
                                      UserLoginAccount userLoginAccount,
                                      String sessionId,
                                      UserHttpSessions userHttpSessions) {

    // 实例id
    int instanceId = getNextInstanceId(sessionId, userHttpSessions);
    machine.setStatusCd(Machine.SUCCESS_STATUS);
    machine.setInstanceId(instanceId);

    SchSession schSession = null;

    try {
      Session session = createSession(machine, userLoginAccount);

      // 在session上新建指定类型的channel
      Channel channel = session.openChannel("shell");
      ((ChannelShell) channel).setAgentForwarding(agentForwarding);
      ((ChannelShell) channel).setPtyType("xterm");
      // 连接channel
      channel.connect();
      
      // channel的输出
      InputStream outFromChannel = channel.getInputStream();


      // new session output, session输出对象,保存session在machine的输出
      SessionOutput sessionOutput = new SessionOutput(sessionId, machine);


      // 创建监听线程
      Runnable run = new SecureShellTask(sessionOutput, outFromChannel);
      Thread thread = new Thread(run);
      thread.start();


      // Gets an OutputStream for this channel.
      // All data written to this stream will be sent in SSH_MSG_CHANNEL_DATA
      // messages to the remote side. This method is an alternative to
      // setInputStream(java.io.InputStream). It should be called before connect().
      OutputStream inputToChannel = channel.getOutputStream();
      PrintStream commander = new PrintStream(inputToChannel, true);


      // schSession
      schSession = new SchSession();
      schSession.setUserId(userLoginAccount.getUserId());
      schSession.setSession(session);
      schSession.setChannel(channel);
      schSession.setCommander(commander);
      schSession.setInputToChannel(inputToChannel);
      schSession.setOutFromChannel(outFromChannel);
      schSession.setMachine(machine);

    } catch (Exception e) {
      log.error("openSSHTerm failed.", e);
      machine.setErrorMsg(e.getMessage());
      if (e.getMessage().toLowerCase().contains("userauth fail")) {
        machine.setStatusCd(Machine.PUBLIC_KEY_FAIL_STATUS);
      } else if (e.getMessage().toLowerCase().contains("auth fail") || e.getMessage().toLowerCase().contains("auth cancel")) {
        machine.setStatusCd(Machine.AUTH_FAIL_STATUS);
      } else if (e.getMessage().toLowerCase().contains("unknownhostexception")){
        machine.setErrorMsg("DNS Lookup Failed");
        machine.setStatusCd(Machine.HOST_FAIL_STATUS);
      } else {
        machine.setStatusCd(Machine.GENERIC_FAIL_STATUS);
      }
    }

    //add session to map, 将session添加到全局map
    if (machine.getStatusCd().equals(Machine.SUCCESS_STATUS)) {
      //get the server maps for user
      UserSchSessions userSchSessions = userHttpSessions.get(sessionId);

      //if no user session create a new one, 不存在则创建
      if (userSchSessions == null) {
        userSchSessions = new UserSchSessions();
      }

      //add server information
      userSchSessions.put(instanceId, schSession);

      //add back to map
      userHttpSessions.put(sessionId, userSchSessions);
    }

    machineService.update(machine);

    return machine;
  }

  /**
   * return the next instance id based on ids defined in the session map
   *
   * @param sessionId      session id
   * @param userHttpSessions user session map
   * @return
   */
  private int getNextInstanceId(String sessionId, UserHttpSessions userHttpSessions) {
    Integer instanceId = 1;
    if (userHttpSessions.keySet().contains(sessionId) && userHttpSessions.get(sessionId) != null){

      for(Integer id : userHttpSessions.get(sessionId).keySet()) {
        if (!id.equals(instanceId) && userHttpSessions.get(sessionId).get(instanceId) == null) {
          return instanceId;
        }
        instanceId = instanceId + 1;
      }
    }
    return instanceId;
  }

  /**
   * distributes uploaded item to system defined
   *
   * @param machine     object contains host system information
   * @param source      source file
   * @param destination destination file
   * @return status uploaded file
   */
  public Machine pushUpload(Machine machine,
                            UserLoginAccount userLoginAccount,
                            String source,
                            String destination) {

    machine.setStatusCd(Machine.SUCCESS_STATUS);

    Session session = null;
    try {
      session = createSession(machine, userLoginAccount);
    } catch (Exception e) {
      log.error(e.toString(), e);
      machine.setErrorMsg(e.getMessage());
      machine.setStatusCd(Machine.GENERIC_FAIL_STATUS);
      return machine;
    }

    Channel channel = null;
    ChannelSftp c = null;

    try (FileInputStream file = new FileInputStream(source)) {
      channel = session.openChannel("sftp");
      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);
      channel.connect(CHANNEL_TIMEOUT);

      c = (ChannelSftp) channel;
      destination = destination.replaceAll("~\\/|~", "");

      c.put(file, destination);

    } catch (Exception e) {
      log.error(e.toString(), e);
      machine.setErrorMsg(e.getMessage());
      machine.setStatusCd(Machine.GENERIC_FAIL_STATUS);
    }
    //exit
    if (c != null) {
      c.exit();
    }
    // disconnect
    if (channel != null) {
      channel.disconnect();
    }

    // disconnect
    if (session != null) {
      session.disconnect();
    }

    return machine;
  }

  /**
   * 建立本机与机器machine的连接
   * @param machine
   * @return
   * @throws JSchException
   * @throws IOException
   */
  public Session createSession(Machine machine, UserLoginAccount userLoginAccount) throws JSchException, IOException {
    JSch jsch = new JSch();
    Session session = jsch.getSession(userLoginAccount.getAccount(), machine.getHost(), machine.getPort());
    session.setPassword(EncryptionUtil.decrypt(userLoginAccount.getPassword()));
    session.setConfig("StrictHostKeyChecking", "no");
    session.setConfig("PreferredAuthentications", "password");
    session.setServerAliveInterval(SERVER_ALIVE_INTERVAL);
    session.connect(SESSION_TIMEOUT);
    return session;
  }

  /**
   * 连接终端:异步方式
   * @param machine
   * @param userLoginAccount
   * @param sessionId
   * @param userHttpSessions
   */
  @Async
  public void openSSHTermOnMachineAsync(Machine machine,
                                        UserLoginAccount userLoginAccount,
                                        String sessionId,
                                        UserHttpSessions userHttpSessions) {

    // 实例id

    SessionMachineStatus sessionMachineStatus = new SessionMachineStatus();
    sessionMachineStatus.setSessionId(sessionId);
    sessionMachineStatus.setInstanceId(machine.getInstanceId());
    sessionMachineStatus.setMachineId(machine.getId());
    sessionMachineStatus.setStatusCd(Machine.INITIAL_STATUS);
    sessionMachineStatusService.insertAndSetObjectId(sessionMachineStatus);

    SchSession schSession = null;

    try {
      Session session = createSession(machine, userLoginAccount);

      // 在session上新建指定类型的channel
      Channel channel = session.openChannel("shell");
      ((ChannelShell) channel).setAgentForwarding(agentForwarding);
      ((ChannelShell) channel).setPtyType("xterm");
      // 连接channel
      channel.connect();

      // channel的输出
      InputStream outFromChannel = channel.getInputStream();

      // new session output, session输出对象,保存session在machine的输出
      SessionOutput sessionOutput = new SessionOutput(sessionId, machine);


      // 创建监听线程
      Runnable run = new SecureShellTask(sessionOutput, outFromChannel);
      Thread thread = new Thread(run);
      thread.start();


      // Gets an OutputStream for this channel.
      OutputStream inputToChannel = channel.getOutputStream();
      PrintStream commander = new PrintStream(inputToChannel, true);


      // schSession
      schSession = new SchSession();
      schSession.setUserId(userLoginAccount.getUserId());
      schSession.setSession(session);
      schSession.setChannel(channel);
      schSession.setCommander(commander);
      schSession.setMachine(machine);
      schSession.setInputToChannel(inputToChannel);
      schSession.setOutFromChannel(outFromChannel);

      sessionMachineStatus.setStatusCd(Machine.SUCCESS_STATUS);

    } catch (Exception e) {
      log.error("openSSHTerm failed.", e);
      sessionMachineStatus.setErrorMsg(e.getMessage());

      if (e.getMessage().toLowerCase().contains("userauth fail")) {
        sessionMachineStatus.setStatusCd(Machine.PUBLIC_KEY_FAIL_STATUS);
      } else if (e.getMessage().toLowerCase().contains("auth fail") || e.getMessage().toLowerCase().contains("auth cancel")) {
        sessionMachineStatus.setStatusCd(Machine.AUTH_FAIL_STATUS);
      } else if (e.getMessage().toLowerCase().contains("unknownhostexception")){
        sessionMachineStatus.setStatusCd(Machine.HOST_FAIL_STATUS);
      } else {
        sessionMachineStatus.setStatusCd(Machine.GENERIC_FAIL_STATUS);
      }
    }

    //add session to map, 将session添加到全局map
    if (sessionMachineStatus.getStatusCd().equals(Machine.SUCCESS_STATUS)) {
      UserSchSessions userSchSessions = userHttpSessions.get(sessionId);
      if (userSchSessions == null) {
        userSchSessions = new UserSchSessions();
      }

      userSchSessions.put(machine.getInstanceId(), schSession);

      userHttpSessions.put(sessionId, userSchSessions);
    }

    sessionMachineStatusService.update(sessionMachineStatus);
  }
}
