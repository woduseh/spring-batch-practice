package com.example.springbatchpractice.config;

import com.example.springbatchpractice.quartz.CustomQuartzJob;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

// From https://howtodoinjava.com/spring-batch/batch-quartz-java-config-example/

/**
 * Run UserJob by Quarts Scheduler
 *
 * @author Hwang Jae Yeon
 */
@Configuration
public class QuartzConfig {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private JobLocator jobLocator;

  @Bean
  public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
    jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
    return jobRegistryBeanPostProcessor;
  }

  @Bean
  public JobDetail simpleJobDetail() {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "simpleJob");
    jobDataMap.put("requestDate", LocalDateTime.now().toString());
    return jobDetailMaker(jobDataMap, "simpleJob");
  }

  @Bean
  public Trigger simpleJobTrigger() {
    CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
        .cronSchedule("2/5 * * * * ?");
    return triggerMaker(simpleJobDetail(), cronScheduleBuilder, "simpleJobTrigger");
  }

  @Bean
  public JobDetail userCreateJobDetail() {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "userCreateJob");
    jobDataMap.put("user_size", 100);
    jobDataMap.put("base_amount", -1);
    jobDataMap.put("money", 1000000);
    return jobDetailMaker(jobDataMap, "userCreateJob");
  }

  @Bean
  public JobDetail userMoneyGambleJobDetail() {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "userMoneyGambleJob");
    jobDataMap.put("base_amount", 500000);
    return jobDetailMaker(jobDataMap, "userMoneyGambleJob");
  }

  @Bean
  public Trigger userCreateJobTrigger() {
    SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder
        .simpleSchedule()
        .withIntervalInSeconds(1)
        .withRepeatCount(1);
    return triggerMaker(userCreateJobDetail(), simpleScheduleBuilder, "userCreateJobTrigger");
  }

  @Bean
  public Trigger userMoneyGambleTrigger() {
    CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
        .cronSchedule("0/30 * * 1/1 * ? *");
    return triggerMaker(userMoneyGambleJobDetail(), cronScheduleBuilder,
        "userMoneyGambleJobTrigger");
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    scheduler.setTriggers(userCreateJobTrigger(), userMoneyGambleTrigger());
    scheduler.setQuartzProperties(quartzProperties());
    scheduler.setJobDetails(userCreateJobDetail(), userMoneyGambleJobDetail());
    return scheduler;
  }

  @Bean
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }

  private JobDetail jobDetailMaker(JobDataMap jobDataMap, String jobName) {
    jobDataMap.put("jobLauncher", jobLauncher);
    jobDataMap.put("jobLocator", jobLocator);

    return JobBuilder.newJob(CustomQuartzJob.class)
        .withIdentity(jobName)
        .setJobData(jobDataMap)
        .storeDurably()
        .build();
  }

  private Trigger triggerMaker(JobDetail jobDetail, ScheduleBuilder schedule,
      String triggerName) {
    return TriggerBuilder
        .newTrigger()
        .forJob(jobDetail)
        .withIdentity(triggerName)
        .withSchedule(schedule)
        .build();
  }
}