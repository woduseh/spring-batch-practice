package com.example.springbatchpractice.job;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.springbatchpractice.config.TestBatchConfig;
import com.example.springbatchpractice.dao.UserRepository;
import com.example.springbatchpractice.entity.User;
import java.time.LocalDate;
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
 * fileName         : UserDeleteJobTest
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : UserDeleteJob의 정상 작동 확인을 위한 테스트 코드
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
@SpringBootTest(classes = {TestBatchConfig.class, UserDeleteJobConfiguration.class})
class UserDeleteJobTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @AfterEach
  public void tearDown() {
    userRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("Delete_Date Update Test")
  void deleteTest() throws Exception {
    // given
    long money = 1000000;
    LocalDate deleteRes = LocalDate.now();

    userRepository.save(new User("황재연1", money, deleteRes));
    userRepository.save(new User("황재연2", money, LocalDate.of(2022, 1, 9)));
    userRepository.save(new User("황재연3", money, LocalDate.of(2022, 1, 13)));

    JobParameters jobParameters = new JobParametersBuilder()
        .addString("unique Parameter", LocalDateTime.now().toString())
        .toJobParameters();

    // When
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // Then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    List<User> userList = userRepository.findAll();
    assertThat(userList.size()).isEqualTo(3);
    assertThat(userList.get(0).getDeleteDate()).isEqualTo(deleteRes);
    assertThat(userList.get(1).getDeleteDate()).isNull();
    assertThat(userList.get(2).getDeleteDate()).isNull();
  }
}
