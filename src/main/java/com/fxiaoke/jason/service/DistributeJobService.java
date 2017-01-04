package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.*;
import com.fxiaoke.jason.task.DistributeTask;
import com.fxiaoke.jason.task.UploadTask;
import com.github.shiro.support.ShiroCasRealm;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by alex on 2016/11/18.
 */
@Slf4j
@Service
public class DistributeJobService {

  @Autowired
  private ShiroCasRealm shiroCasRealm;

  @Autowired
  private ConfigService configService;

  @Autowired
  private JobService jobService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private MachineService machineService;

  @Autowired
  private JobTaskMapService jobTaskMapService;

  @Autowired
  private SSHService sshService;

  public static final String JOB_UPLOAD = "JOB_UPLOAD";
  public static final String JOB_DISTRIBUTE = "JOB_DISTRIBUTE";

  /**
   * 运行部署任务
   *
   * @param request
   * @param machineIds
   * @param destination
   * @param userLoginAccount
   * @return
   */
  public Long upload(HttpServletRequest request,
                     List<Long> machineIds,
                     String destination,
                     UserLoginAccount userLoginAccount) {
    // 任务开始
    Job job = new Job();
    job.setBeginDt(DateTime.now().toDate());
    job.setJobType(JOB_UPLOAD);
    job.setStatusCd(Job.PENDING);
    jobService.insertAndSetObjectId(job);

    // 拆分任务
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getServletContext());
    String userName = shiroCasRealm.getCurrentUserName();

    List<String> uploadFileNames = Lists.newLinkedList();

    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();//创建一个缓冲线程池

    // 检查form中是否有enctype="multipart/form-data"
    if (multipartResolver.isMultipart(request)) {
      // 将request变成多部分request
      MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

      // 获取multiRequest 中所有的文件名
      Iterator iter = multipartHttpServletRequest.getFileNames();

      while (iter.hasNext()) {
        // 一次遍历所有文件
        MultipartFile multipartFile = multipartHttpServletRequest.getFile(iter.next().toString());
        if (multipartFile != null) {

          // 文件名
          String uploadFileName = multipartFile.getOriginalFilename();
          if (!Strings.isNullOrEmpty(uploadFileName)) {
            uploadFileNames.add(uploadFileName);

            // 存储的文件
            Path userTmpPath = Paths.get(configService.getTmpUploadDir(), userName, uploadFileName);
            File userTmpTarget = userTmpPath.toFile();

            try {
              // 上传到本地
              Task task = new Task();
              task.setNm("Upload");
              task.setDesc("Upload file to disk");
              task.setBeginDt(DateTime.now().toDate());
              task.setTaskType(JOB_UPLOAD);
              task.setStatusCd(Task.RUNNING);
              taskService.insertAndSetObjectId(task);

              // 任务与子任务的映射关系
              JobTaskMap jobTaskMap = new JobTaskMap();
              jobTaskMap.setJobId(job.getId());
              jobTaskMap.setTaskId(task.getId());
              jobTaskMapService.insert(jobTaskMap);

              Runnable run = new UploadTask(multipartFile, userTmpTarget, task, taskService);
              Thread thread = new Thread(run);
              thread.start();
              thread.join();

              // 上传到slave机器的目的路径
              File remoteTarget = new File(destination, uploadFileName);

              for (Long machineId : machineIds) {
                Machine machine = machineService.findById(machineId);

                task = new Task();
                task.setNm("Distribute");
                task.setDesc(MessageFormatter.format("Distribute {} to {}", uploadFileName, machine.getHost())
                                             .getMessage());
                task.setBeginDt(DateTime.now().toDate());
                task.setTaskType(JOB_DISTRIBUTE);
                task.setStatusCd(Task.RUNNING);
                taskService.insertAndSetObjectId(task);

                // 任务与子任务的映射关系
                jobTaskMap = new JobTaskMap();
                jobTaskMap.setTaskId(task.getId());
                jobTaskMap.setJobId(job.getId());
                jobTaskMapService.insert(jobTaskMap);

                // 运行task
                run =
                  new DistributeTask(machine, userLoginAccount, userTmpTarget.toString(), remoteTarget.toString(), task, taskService, sshService);
                cachedThreadPool.execute(run);
              }
            } catch (Exception e) {
              log.error(e.toString(), e);
            }
          }
        }
      }
    }
    return job.getId();
  }

  /**
   * 上传文件
   *
   * @param multipartFile
   * @param targetFile
   * @param task
   * @return
   */
  private boolean uploadSourceFile(MultipartFile multipartFile, File targetFile, Task task) {
    String parentPath = targetFile.getParent();
    File parentDir = new File(parentPath);
    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }

    boolean isUploadSuccess;

    try {
      multipartFile.transferTo(targetFile);

      // 保存状态
      task.setEndDt(DateTime.now().toDate());
      task.setStatusCd(Task.SUCCESS);
      taskService.update(task);

      isUploadSuccess = true;

    } catch (IOException e) {
      e.printStackTrace();

      // 保存状态
      task.setEndDt(DateTime.now().toDate());
      task.setStatusCd(Task.FAILED);
      taskService.update(task);

      isUploadSuccess = false;
    }

    return isUploadSuccess;
  }

}
