package com.example.springbatchpractice.job.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class CalculatorJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job calculatorJob() {
    return jobBuilderFactory.get("calculatorJob")
        .start(calculatorStep(null, null))
        .build();
  }

  @Bean
  @JobScope
  public Step calculatorStep(@Value("#{jobParameters[value1]}") Double value1,
      @Value("#{jobParameters[value2]}") Double value2) {
    return stepBuilderFactory.get("calculatorStep")
        .tasklet((contribution, chunkContext) -> {
          log.info(">>>>> calculatorStep");
          log.info("value1 : {} value2 : {}", value1, value2);
          log.info("value3 : {}", value1 + value2);  // 단순히 두개의 파라미터를 더하여 출력

          return RepeatStatus.FINISHED;
        }).build();
  }
}