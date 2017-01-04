package com.fxiaoke.jason.util;

import com.fxiaoke.jason.entity.SessionOutput;
import com.fxiaoke.jason.entity.UserHttpSessionsOutput;
import com.fxiaoke.jason.entity.UserHttpSessionsOutputFactory;
import com.fxiaoke.jason.entity.UserSchSessionsOutput;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2016/10/11.
 */
public class SessionOutputUtil {

  //@Autowired
  //private TerminalLogService terminalLogService;

  //private static boolean enableInternalAudit = false;

  private static Logger log = LoggerFactory.getLogger(SessionOutputUtil.class);

  private static UserHttpSessionsOutput userHttpSessionsOutput = UserHttpSessionsOutputFactory.getInstance();
  //private static Map<Long, UserSchSessionsOutput> userSessionsOutputMap = new ConcurrentHashMap<>();
  //private static Logger systemAuditLogger = LoggerFactory.getLogger("com.keybox.manage.util.SystemAudit");

  /**
   * removes session for user session
   *
   * @param sessionId session id
   */
  public static void removeUserSession(String sessionId) {
    UserSchSessionsOutput userSessionsOutput = userHttpSessionsOutput.get(sessionId);
    if (userSessionsOutput != null) {
      userSessionsOutput.clear();
    }
    userSessionsOutput.remove(sessionId);

  }

  /**
   * removes session output for host system
   *
   * @param sessionId    session id
   * @param instanceId id of host system instance
   */
  public static void removeOutput(String sessionId, Integer instanceId) {

    UserSchSessionsOutput userSessionsOutput = userHttpSessionsOutput.get(sessionId);
    if (userSessionsOutput != null) {
      userHttpSessionsOutput.remove(instanceId);
    }
  }

  /**
   * adds a new output
   *
   * @param sessionOutput session output object
   */
  public static void addOutput(SessionOutput sessionOutput) {

    UserSchSessionsOutput userSessionsOutput = userHttpSessionsOutput.get(sessionOutput.getSessionId());
    if (userSessionsOutput == null) {
      userHttpSessionsOutput.put(sessionOutput.getSessionId(), new UserSchSessionsOutput());
      userSessionsOutput = userHttpSessionsOutput.get(sessionOutput.getSessionId());
    }
    userSessionsOutput.put(sessionOutput.getInstanceId(), sessionOutput);
  }


  /**
   * adds a new output
   *
   * @param sessionId    session id
   * @param instanceId id of host system instance
   * @param value        Array that is the source of characters
   * @param offset       The initial offset
   * @param count        The length
   */
  public static void addToOutput(String sessionId, Integer instanceId, char value[], int offset, int count) {

    UserSchSessionsOutput userSessionsOutput = userHttpSessionsOutput.get(sessionId);
    if (userSessionsOutput != null) {
      userSessionsOutput.get(instanceId).getOutput().append(value, offset, count);
    }

  }


  /**
   * returns list of output lines
   *
   * @param sessionId session id object
   * @return session output list
   */
  public static List<SessionOutput> getOutput(String sessionId) {
    List<SessionOutput> outputList = new ArrayList<>();

    // 从全局map中获取session
    UserSchSessionsOutput userSessionsOutput = userHttpSessionsOutput.get(sessionId);
    if (userSessionsOutput != null) {

      for (Integer key : userSessionsOutput.keySet()) {

        //get output chars and set to output
        try {
          SessionOutput sessionOutput = userSessionsOutput.get(key);
          if (sessionOutput != null && sessionOutput.getOutput() != null
            && !Strings.isNullOrEmpty(String.valueOf(sessionOutput.getOutput()))) {

            outputList.add(sessionOutput);

            //send to audit logger
            //systemAuditLogger.info(gson.toJson(new AuditWrapper(user, sessionOutput)));

            /**
            if(enableInternalAudit) {
              TerminalLog terminalLog = new TerminalLog();
              terminalLog.setSessionId(sessionOutput.getSessionId());
              terminalLog.setMachineId(sessionOutput.getId());
              terminalLog.setInstanceId(sessionOutput.getInstanceId());
              terminalLog.setOutput(sessionOutput.getOutput().toString());
              terminalLogService.insert(terminalLog);
            }
             */

            userSessionsOutput.put(key, new SessionOutput(sessionId, sessionOutput));
          }
        } catch (Exception ex) {
          log.error("getOutput() error: {}", ex.toString(), ex);
        }
      }
    }

    return outputList;
  }
}
