package com.example.springbatchpractice.job;

import com.example.springbatchpractice.entity.User;
import com.example.springbatchpractice.util.CreateDateJobParameter;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.ObjectUtils;

/**
 * <pre>
 * packageName      : com.example.springbatchpractice.job
 * fileName         : UserNoticeDeleteResJobConfiguration
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : 입력한 삭제 예정일 (deleteRes)보다 삭제 예정일이 적게 남은 User들에게 삭제 예정 알림을 보내는 배치 작업
 * </pre>
 * ===========================================================
 * <pre>
 * DATE                 AUTHOR                  NOTE
 * -----------------------------------------------------
 * 2022-01-10           JYHwang                 최초 생성
 * 2022-01-11           JYHwang                 불필요한 코드 개선
 * </pre>
 */

@Slf4j
@RequiredArgsConstructor
@Configuration
public class UserNoticeDeleteResJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  private final DataSource dataSource;

  private static final int CHUNK_SIZE = 1000;

  @Bean
  @JobScope
  public CreateDateJobParameter jobParameter() {
    return new CreateDateJobParameter();
  }

  @Bean
  Job userNoticeDeleteResJob() throws Exception {
    return jobBuilderFactory.get("userNoticeDeleteResJob")
        .start(userNoticeDeleteResStep())
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  @JobScope
  @Transactional
  public Step userNoticeDeleteResStep()
      throws Exception {
    log.info(">>>>> userNoticeDeleteResStep");
    return stepBuilderFactory.get("userNoticeDeleteResStep")
        .<User, User>chunk(CHUNK_SIZE)
        .reader(userNoticeDeleteResReader(null))
        .processor(userNoticeDeleteResProcessor())
        .writer(userNoticeDeleteResWriter())
        .build();
  }

  @Bean
  @StepScope
  public JdbcPagingItemReader<User> userNoticeDeleteResReader(
      @Value("#{jobParameters[dueDate]}") Integer dueDate) throws Exception {
    log.info(">>>>> userNoticeDeleteResReader working");

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("dueDate", dueDate);

    return new JdbcPagingItemReaderBuilder<User>()
        .pageSize(CHUNK_SIZE)
        .fetchSize(CHUNK_SIZE)
        .dataSource(dataSource)
        .rowMapper(new BeanPropertyRowMapper<>(User.class))
        .queryProvider(selectUserNoticeDeleteResQueryProvider())
        .parameterValues(parameterValues)
        .name("userNoticeDeleteResReader")
        .build();
  }

  @Bean
  public PagingQueryProvider selectUserNoticeDeleteResQueryProvider() throws Exception {
    SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
    queryProvider.setDataSource(dataSource); // Database에 맞는 PagingQueryProvider를 선택하기 위해
    queryProvider.setSelectClause("*");
    queryProvider.setFromClause("from user");
    queryProvider.setWhereClause("where DATEDIFF(delete_res, now()) <= :dueDate");

    Map<String, Order> sortKeys = new HashMap<>(1);
    sortKeys.put("id", Order.ASCENDING);

    queryProvider.setSortKeys(sortKeys);

    return queryProvider.getObject();
  }

  @Bean
  @StepScope
  public ItemProcessor<User, User> userNoticeDeleteResProcessor() {
    log.info(">>>>> userNoticeDeleteResProcessor working");
    return user -> {
      if (ObjectUtils.isEmpty(user.getDeleteDate())) {
        Period period = Period.between(LocalDate.now(), user.getDeleteRes());
        log.info("{} 님. 앞으로 {}일 간 접속하지 않으면 계정이 삭제됩니다.", user.getName(), period.getDays());
      }
      return null;
    };
  }


  @Bean
  public JpaItemWriter<User> userNoticeDeleteResWriter() {
    log.info(">>>>> userNoticeDeleteResWriter working");
    JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);

    return jpaItemWriter;
  }
}
