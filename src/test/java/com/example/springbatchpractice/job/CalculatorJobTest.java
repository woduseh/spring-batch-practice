package com.example.springbatchpractice.job;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.springbatchpractice.config.TestBatchConfig;
import com.example.springbatchpractice.job.configuration.CalculatorJobConfiguration;
import java.time.LocalDateTime;
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

@ExtendWith(SpringExtension.class) // JUnit 프레임워크에서 내장된 Runner 실행 시 SpringRunner.class라는 확장된 클래스를 실행
@SpringBatchTest // JobLauncherTestUtils를 사용하기 위해 Spring Batch 4.1 버전에 새롭게 추가된 어노테이션
@SpringBootTest(classes = {CalculatorJobConfiguration.class, TestBatchConfig.class})
// 통합 테스트 실행시 사용할 Java 설정
public class CalculatorJobTest {

  // Batch Job을 테스트 환경에서 실행할 Utils 클래스
  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Test
  @DisplayName("계산기 테스트")
  public void calculatorJobTest() throws Exception {

    double num1 = 2.0;
    double num2 = 3.4;

    JobParameters jobParameters = new JobParametersBuilder()
        .addString("version", LocalDateTime.now().toString())
        .addDouble("value1", num1)
        .addDouble("value2", num2)
        .toJobParameters();

    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // JobParameter와 함께 Job을 실행
    // 해당 Job의 결과는 JobExecution에 담겨 반환
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    // 성공적으로 배치가 수행되었는지 검증
  }
}