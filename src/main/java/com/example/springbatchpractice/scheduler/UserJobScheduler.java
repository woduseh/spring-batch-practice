package com.example.springbatchpractice.scheduler;

import com.example.springbatchpractice.launcher.UserJobLauncher;
import java.io.IOException;
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

// From https://howtodoinjava.com/spring-batch/batch-quartz-java-config-example/
// Crontab : http://www.cronmaker.com/;jsessionid=node0ffb4xaj8ukyo14htyfbj5qzqj3252459.node0?0

/**
 * <pre>
 * packageName      : com.example.springbatchpractice.job
 * fileName         : UserJobScheduler
 * author           : JYHwang
 * date             : 2022-01-11
 * description      : userJob들을 실행하는 quartz scheduler
 * </pre>
 * ===========================================================
 * <pre>
 * DATE                 AUTHOR                  NOTE
 * -----------------------------------------------------
 * 2022-01-11           JYHwang                 최초 생성
 * 2022-01-11           JYHwang                 UserNoticeDeleteRes, UserDelete 작업 추가
 * </pre>
 */

@Configuration
public class UserJobScheduler {

  @Autowired
  JobLauncher jobLauncher;

  @Autowired
  JobLocator jobLocator;

  @Bean
  public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
    jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
    return jobRegistryBeanPostProcessor;
  }

  /**
   * User Create Job 단 1번만 작동
   */
  @Bean
  public JobDetail userCreateJobDetail() {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "userCreateJob");
    jobDataMap.put("user_size", 100);
    jobDataMap.put("base_money", 1000000);
    return jobDetailMaker(jobDataMap, "userCreateJob");
  }

  @Bean
  public Trigger userCreateJobTrigger() {
    SimpleScheduleBuilder userCreateJobSchedule = SimpleScheduleBuilder
        .simpleSchedule()
        .withIntervalInSeconds(1)
        .withRepeatCount(0);

    return TriggerBuilder
        .newTrigger()
        .forJob(userCreateJobDetail())
        .withIdentity("userCreateJobTrigger")
        .withSchedule(userCreateJobSchedule)
        .build();
  }

  /**
   * User Gamble Job 1분마다 작동
   */
  @Bean
  public JobDetail userGambleJobDetail() {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "userGambleJob");
    jobDataMap.put("base_amount", 500000);
    return jobDetailMaker(jobDataMap, "userGambleJob");
  }


  @Bean
  public Trigger userGambleTrigger() {
    CronScheduleBuilder userGambleJobSchedule = CronScheduleBuilder
        .cronSchedule("0 0/1 * 1/1 * ? *");

    return TriggerBuilder
        .newTrigger()
        .forJob(userGambleJobDetail())
        .withIdentity("userGambleJobTrigger")
        .withSchedule(userGambleJobSchedule)
        .build();
  }

  /**
   * User Delete Job 2분마다 작동
   */
  @Bean
  public JobDetail userDeleteJobDetail() {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "userDeleteJob");
    return jobDetailMaker(jobDataMap, "userDeleteJob");
  }


  @Bean
  public Trigger userDeleteTrigger() {
    CronScheduleBuilder userDeleteJobSchedule = CronScheduleBuilder
        .cronSchedule("0 0/2 * 1/1 * ? *");

    return TriggerBuilder
        .newTrigger()
        .forJob(userDeleteJobDetail())
        .withIdentity("userDeleteJobTrigger")
        .withSchedule(userDeleteJobSchedule)
        .build();
  }

  /**
   * User NoticeDeleteRes Job 1분마다 작동 삭제 예정일까지 남은 기간이 <7> 일 미만인 유저에게 알림
   */
  @Bean
  public JobDetail userNoticeDeleteResJobDetail() {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "userNoticeDeleteResJob");
    jobDataMap.put("dueDate", 7);
    return jobDetailMaker(jobDataMap, "userNoticeDeleteResJob");
  }


  @Bean
  public Trigger userNoticeDeleteResTrigger() {
    CronScheduleBuilder userNoticeDeleteResJobSchedule = CronScheduleBuilder
        .cronSchedule("0 0/1 * 1/1 * ? *");

    return TriggerBuilder
        .newTrigger()
        .forJob(userNoticeDeleteResJobDetail())
        .withIdentity("userNoticeDeleteResTrigger")
        .withSchedule(userNoticeDeleteResJobSchedule)
        .build();
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    scheduler.setQuartzProperties(quartzProperties());
    scheduler.setJobDetails(userCreateJobDetail(), userGambleJobDetail(), userDeleteJobDetail(),
        userNoticeDeleteResJobDetail());
    scheduler.setTriggers(userCreateJobTrigger(), userGambleTrigger(), userDeleteTrigger(),
        userNoticeDeleteResTrigger());
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

    return JobBuilder.newJob(UserJobLauncher.class)
        .withIdentity(jobName)
        .setJobData(jobDataMap)
        .storeDurably()
        .build();
  }
}