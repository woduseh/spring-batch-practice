package com.example.springbatchpractice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/*
 * https://howtodoinjava.com/spring-batch/spring-batch-event-listeners/
 * */

@Slf4j
public class JobResultListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.info("New Job Started.");
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("Job Success.");
    } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
      log.info("Job Failure.");
    }
  }
}