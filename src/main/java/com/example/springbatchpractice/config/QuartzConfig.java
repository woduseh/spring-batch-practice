package com.example.springbatchpractice.config;

import com.example.springbatchpractice.quartz.CustomQuartzJob;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
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

/*
 * https://howtodoinjava.com/spring-batch/batch-quartz-java-config-example/
 *
 * */

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
  public JobDetail jobOneDetail() {
    //Set Job data map
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "simpleJob");
    jobDataMap.put("requestDate", LocalDateTime.now().toString());
    jobDataMap.put("jobLauncher", jobLauncher);
    jobDataMap.put("jobLocator", jobLocator);

    return JobBuilder.newJob(CustomQuartzJob.class)
        .withIdentity("simpleJob")
        .setJobData(jobDataMap)
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger jobOneTrigger() {
    CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
        .cronSchedule("2/5 * * * * ?");

    SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
        .simpleSchedule()
        .withIntervalInSeconds(10)
        .withRepeatCount(3);

    return TriggerBuilder
        .newTrigger()
        .forJob(jobOneDetail())
        .withIdentity("jobOneTrigger")
        .withSchedule(cronScheduleBuilder)
        .build();
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
//    scheduler.setTriggers(jobOneTrigger(), jobTwoTrigger());
    scheduler.setTriggers(jobOneTrigger());
    scheduler.setQuartzProperties(quartzProperties());
//    scheduler.setJobDetails(jobOneDetail(), jobTwoDetail());
    scheduler.setJobDetails(jobOneDetail());
    return scheduler;
  }

  @Bean
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }
}