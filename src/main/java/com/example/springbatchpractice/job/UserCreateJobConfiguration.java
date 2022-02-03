package com.example.springbatchpractice.job;

import com.example.springbatchpractice.dao.UserRepository;
import com.example.springbatchpractice.entity.User;
import com.example.springbatchpractice.util.DateUtil;
import java.time.LocalDate;
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

/**
 * <pre>
 * packageName      : com.example.springbatchpractice.job
 * fileName         : UserCreateJobConfiguration
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : 입력한 숫자만큼 user를 생성하는 기능
 * </pre>
 * ===========================================================
 * <pre>
 * DATE                 AUTHOR                  NOTE
 * -----------------------------------------------------
 * 2022-01-10           JYHwang                 최초 생성
 * </pre>
 */

@Slf4j
@RequiredArgsConstructor
@Configuration
public class UserCreateJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final UserRepository userRepository;
  private final Random random = new Random();

  @Bean
  public Job userCreateJob() {
    return jobBuilderFactory.get("userCreateJob")
        .start(userCreateStep(null, null))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  @JobScope
  public Step userCreateStep(@Value("#{jobParameters[user_size]}") Long userSize,
      @Value("#{jobParameters[base_money]}") Long baseMoney) {
    return stepBuilderFactory.get("userCreateStep")
        .tasklet((contribution, chunkContext) -> {
          log.info(">>>>> userCreateStep");

          for (Long i = 1L; i <= userSize; i++) {
            String name = nameMaker(random.nextInt(7));
            LocalDate deleteRes = DateUtil.randomTimeMaker();
            User user = new User(name, baseMoney, deleteRes);
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
