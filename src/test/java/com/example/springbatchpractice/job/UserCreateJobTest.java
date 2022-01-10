package com.example.springbatchpractice.job;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.springbatchpractice.config.TestBatchConfig;
import com.example.springbatchpractice.dao.UserRepository;
import com.example.springbatchpractice.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * <pre>
 * packageName      : com.example.springbatchpractice.job
 * fileName         : UserCreateJobTest
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : UserCreateJob의 작동 확인을 위한 테스트 코드
 * </pre>
 * ===========================================================
 * <pre>
 * DATE                 AUTHOR                  NOTE
 * -----------------------------------------------------
 * 2022-01-10           JYHwang                 최초 생성
 * </pre>
 */

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(classes = {TestBatchConfig.class, UserCreateJobConfiguration.class})
public class UserCreateJobTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @AfterEach
  public void tearDown() {
    userRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("User Create Test")
  public void createTest() throws Exception {
    // given
    Long userSize = 1L;
    Long baseMoney = 1000000L;

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("user_size", userSize)
        .addLong("base_money", baseMoney)
        .addString("unique Parameter", LocalDateTime.now().toString())
        .toJobParameters();

    // When
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // Then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    List<User> userList = userRepository.findAll();
    assertThat(userList.size()).isEqualTo(1);
    assertThat(userList.get(0).getMoney()).isEqualTo(baseMoney);
  }
}
