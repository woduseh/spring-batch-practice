package com.example.springbatchpractice.job;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.springbatchpractice.config.TestBatchConfig;
import com.example.springbatchpractice.dao.UserRepository;
import com.example.springbatchpractice.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * fileName         : UserNoticeDeleteResJobTest
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : UserNoticeDeleteResJob의 작동 확인을 위한 테스트 코드
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
@SpringBootTest(classes = {TestBatchConfig.class, UserNoticeDeleteResJobConfiguration.class})
class UserNoticeDeleteResJobTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @AfterEach
  public void tearDown() {
    userRepository.deleteAllInBatch();
  }

  /**
   * application.yml의 profile.active 옵션이 local이면 실패함 (sql 문법이 mysql에 맞추어진 까닭에 옵션을 mysql로 변경하면 성공)
   */
  @Test
  @DisplayName("Notice Delete_Res")
  void UserNoticeDeleteResTest() throws Exception {
    // given
    long money = 1000000;
    LocalDate deleteRes = LocalDate.of(2022, 1, 13);

    userRepository.save(new User("황재연", money, deleteRes));

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("dueDate", 7L)
        .addString("unique Parameter", LocalDateTime.now().toString())
        .toJobParameters();

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
  }
}
