package com.example.springbatchpractice.job;

import com.example.springbatchpractice.entity.User;
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

/**
 * <pre>
 * packageName      : com.example.springbatchpractice.job
 * fileName         : UserNoticeDeleteResJobConfiguration
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : 삭제 예졍일이
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
public class UserNoticeDeleteResJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  private final DataSource dataSource;

  private static final int CHUNK_SIZE = 1000;

  @Bean
  Job UserNoticeDeleteResJob() throws Exception {
    return jobBuilderFactory.get("userNoticeDeleteResJob")
        .start(userNoticeDeleteResStep(null))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  @JobScope
  @Transactional
  public Step userNoticeDeleteResStep(@Value("#{jobParameters[delete_res]}") LocalDate deleteRes)
      throws Exception {
    log.info(">>>>> userMoneyIncreaseStep");
    return stepBuilderFactory.get("userMoneyIncreaseStep")
        .<User, User>chunk(CHUNK_SIZE)
        .reader(userInfoReader(null))
        .processor(userNoticeDeleteResProcessor())
        .writer(userInfoWriter())
        .build();
  }

  @Bean
  @StepScope
  public JdbcPagingItemReader<User> userInfoReader(
      @Value("#{jobParameters[deleteRes]}") LocalDate deleteRes
  ) throws Exception {
    log.info(">>>>> userInfoReader working");

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("deleteRes", deleteRes);

    return new JdbcPagingItemReaderBuilder<User>()
        .pageSize(CHUNK_SIZE)
        .fetchSize(CHUNK_SIZE)
        .dataSource(dataSource)
        .rowMapper(new BeanPropertyRowMapper<>(User.class))
        .queryProvider(createQueryProvider())
        .parameterValues(parameterValues)
        .name("userInfoReader")
        .build();
  }

  @Bean
  public PagingQueryProvider createQueryProvider() throws Exception {
    SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
    queryProvider.setDataSource(dataSource); // Database에 맞는 PagingQueryProvider를 선택하기 위해
    queryProvider.setSelectClause("*");
    queryProvider.setFromClause("from user");
    queryProvider.setWhereClause(
        "where delete_date is null and DATEDIFF(now(), delete_res) <= deleteRes");

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
      Period period = Period.between(user.getDeleteRes(), LocalDate.now());
      log.info("{} 님. 앞으로 {}일 간 접속하지 않으면 계정이 삭제됩니다.", user.getName(), period.getDays());
      return null;
    };
  }


  @Bean
  public JpaItemWriter<User> userInfoWriter() {
    log.info(">>>>> userInfoWriter working");
    JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);

    return jpaItemWriter;
  }
}
