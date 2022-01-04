package com.example.springbatchpractice.quartz;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Getter
@Setter
@Slf4j
public class CustomQuartzJob extends QuartzJobBean {

  private String jobName;
  private JobLauncher jobLauncher;
  private JobLocator jobLocator;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

    Map<String, Object> jobDataMap = context.getMergedJobDataMap();

    try {
      Job job = jobLocator.getJob(jobName);
      JobParameters params = getJobParametersFromJobMap(jobDataMap);

      jobLauncher.run(job, params);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected JobParameters getJobParametersFromJobMap(Map<String, Object> jobDataMap) {

    // Job Parameter 중복으로 인한 실행 에러를 막기 위해 Unique Parameter를 삽입
    jobDataMap.put("JobID", String.valueOf(System.currentTimeMillis()));

    JobParametersBuilder builder = new JobParametersBuilder();

    for (Entry<String, Object> entry : jobDataMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof String && !key.equals("jobName")) {
        builder.addString(key, (String) value);
      } else if (value instanceof Float || value instanceof Double) {
        builder.addDouble(key, ((Number) value).doubleValue());
      } else if (value instanceof Integer || value instanceof Long) {
        builder.addLong(key, ((Number) value).longValue());
      } else if (value instanceof Date) {
        builder.addDate(key, (Date) value);
      } else {
        log.debug(
            "JobDataMap contains values which are not job parameters (ignoring). [key:{}, value:{}]",
            key, value);
      }
    }

    return builder.toJobParameters();
  }
}