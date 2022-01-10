package com.example.springbatchpractice.job;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.springbatchpractice.config.TestBatchConfig;
import com.example.springbatchpractice.dao.UserRepository;
import com.example.springbatchpractice.entity.User;
import com.example.springbatchpractice.util.DateUtil;
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
 * fileName         : UserMoneyIncreaseJobTest
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : UserMoneyIncreaseJob 기능 테스트
 * </pre>
 * ====================================================
 * <pre>
 * DATE                 AUTHOR                  NOTE
 * -----------------------------------------------------
 * 2022-01-10           JYHwang                 최초 생성
 * </pre>
 */

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(classes = {TestBatchConfig.class, UserMoneyIncreaseJobConfiguration.class})
public class UserMoneyIncreaseJobTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @AfterEach
  public void tearDown() {
    userRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("Money Increase")
  public void moneyIncreaseTest() throws Exception {
    // Given
    long money = 1000000;
    long base_amount = 500000;
    LocalDate deleteRes = DateUtil.randomTimeMaker();

    userRepository.save(new User("황재연", money, deleteRes, null));

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("base_amount", base_amount)
        .addLong("money", money)
        .addString("unique Parameter", LocalDateTime.now().toString())
        .toJobParameters();

    // When
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // Then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    List<User> userList = userRepository.findAll();
    assertThat(userList.size()).isEqualTo(1);
    assertThat(userList.get(0).getMoney()).isEqualTo(money + money);
  }
}
