package com.fxiaoke.jason.entity;

import com.github.mybatis.entity.IdEntity;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.PreDestroy;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by alex on 2016/11/18.
 */
@Table(name = "tbl_job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Job extends IdEntity {
  private Long id;
  private Long userId;
  private String jobType;
  private String nm;
  private String desc;
  private Date beginDt;
  private Date endDt;
  private String statusCd;

  @Transient
  public static final String PENDING = "PENDING";

  @Transient
  public static final String RUNNING = "RUNNING";

  @Transient
  public static final String SUCCESS = "SUCCESS";

  @Transient
  public static final String FAILED = "FAILED";

  @Transient
  public List<Long> taskIds = Lists.newLinkedList();

  @Transient
  private ForkJoinPool forkJoinPool = new ForkJoinPool(20);

  @Transient
  @Async
  public void run() {
    try {
      forkJoinPool.submit(() -> taskIds.stream().parallel().forEach(taskId -> {

      })).get();
    } catch (InterruptedException e) {
      log.error("forkJoinPool InterruptedException Error, taskIds: {}", taskIds, e);
    } catch (ExecutionException e) {
      log.error("forkJoinPool ExecutionException Error, taskIds: {}", taskIds, e);
    }
  }

  @Transient
  @PreDestroy
  public void destory() {
    forkJoinPool.shutdown();
  }
}
