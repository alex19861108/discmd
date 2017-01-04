package com.fxiaoke.jason.task;

import com.fxiaoke.jason.entity.Machine;
import com.fxiaoke.jason.entity.Task;
import com.fxiaoke.jason.entity.UserLoginAccount;
import com.fxiaoke.jason.service.SSHService;
import com.fxiaoke.jason.service.TaskService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by alex on 2016/11/18.
 */
@Getter
@Setter
@NoArgsConstructor
public class DistributeTask implements Runnable {

  private TaskService taskService;

  private SSHService sshService;

  private Task task;

  private Machine machine;

  private UserLoginAccount userLoginAccount;

  private String fileFrom;

  private String fileTo;

  public DistributeTask(Machine machine,
                        UserLoginAccount userLoginAccount,
                        String fileFrom,
                        String fileTo,
                        Task task,
                        TaskService taskService,
                        SSHService sshService) {
    this.machine = machine;
    this.userLoginAccount = userLoginAccount;
    this.fileFrom = fileFrom;
    this.fileTo = fileTo;
    this.task = task;

    this.taskService = taskService;
    this.sshService = sshService;
  }

  @Override
  public void run() {

    machine = sshService.pushUpload(machine, userLoginAccount, fileFrom, fileTo);
    String statusCd = machine.getStatusCd();
    Date endDt = DateTime.now().toDate();
    task.setEndDt(endDt);
    task.setStatusCd(statusCd);
    taskService.update(task);
  }
}
