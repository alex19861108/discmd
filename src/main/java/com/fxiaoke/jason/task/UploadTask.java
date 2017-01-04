package com.fxiaoke.jason.task;

import com.fxiaoke.jason.entity.Task;
import com.fxiaoke.jason.service.TaskService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by alex on 2016/11/18.
 */
@Getter
@Setter
@NoArgsConstructor
public class UploadTask implements Runnable {

  private TaskService taskService;

  private MultipartFile multipartFile;
  private File targetFile;
  private Task task;

  public UploadTask(MultipartFile multipartFile, File targetFile, Task task, TaskService taskService) {
    this.multipartFile = multipartFile;
    this.targetFile = targetFile;
    this.task = task;
    this.taskService = taskService;
  }

  @Override
  public void run() {

    String parentPath = targetFile.getParent();
    File parentDir = new File(parentPath);
    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }

    try {
      multipartFile.transferTo(targetFile);

      // 保存状态
      task.setEndDt(DateTime.now().toDate());
      task.setStatusCd(Task.SUCCESS);
      taskService.update(task);

    } catch (IOException e) {
      e.printStackTrace();

      // 保存状态
      task.setEndDt(DateTime.now().toDate());
      task.setStatusCd(Task.FAILED);
      taskService.update(task);
    }
  }
}
