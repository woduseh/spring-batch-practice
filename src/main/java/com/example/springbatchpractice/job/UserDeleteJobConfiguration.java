package com.example.springbatchpractice.job;

import com.example.springbatchpractice.dao.UserRepository;
import com.example.springbatchpractice.entity.User;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * packageName      : com.example.springbatchpractice.job
 * fileName         : UserDeleteJobConfiguration
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : 삭제 예정일이 당일인 user를 논리적으로 삭제하는 기능
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
public class UserDeleteJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final UserRepository userRepository;

  @Bean
  Job userDeleteJob() {
    return jobBuilderFactory.get("userDeleteJob")
        .start(userDeleteStep())
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  @JobScope
  public Step userDeleteStep() {
    return stepBuilderFactory.get("userDeleteStep")
        .tasklet((contribution, chunkContext) -> {
          log.info(">>>>> userDeleteStep");
          List<User> userList = userRepository.findAllByDeleteRes(LocalDate.now());

          for (User user : userList) {
            user.deleteUser();
              log.info("성공적으로 유저를 삭제했습니다. 회원 번호: {} ", user.getId());
          }

          return RepeatStatus.FINISHED;
        }).build();
  }
}
