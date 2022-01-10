package com.example.springbatchpractice.job;

import com.example.springbatchpractice.dao.UserRepository;
import com.example.springbatchpractice.entity.User;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class UserCreateJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final UserRepository userRepository;

  @Bean
  public Job userCreateJob() throws Exception {
    return jobBuilderFactory.get("userCreateJob")
        .start(userCreateStep(null, null))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  @JobScope
  public Step userCreateStep(@Value("#{jobParameters[user_size]}") Integer user_size,
      @Value("#{jobParameters[base_money]}") Long base_money) {
    return stepBuilderFactory.get("userCreateStep")
        .tasklet((contribution, chunkContext) -> {
          log.info(">>>>> userCreateStep");

          for (int i = 1; i <= user_size; i++) {
            String name = nameMaker(new Random().nextInt(7));
            User user = new User(name, base_money);
            log.info(">>>>> {}st user is {}", i, user.getName());
            userRepository.save(user);
          }

          return RepeatStatus.FINISHED;
        }).build();
  }

  private String nameMaker(int index) {
    String[] nameArray = {"김철수", "나은지", "이수지", "홍길동", "임꺽정", "이도", "황재연"};

    return index < nameArray.length ? nameArray[index] : "이름 없음";
  }
}
