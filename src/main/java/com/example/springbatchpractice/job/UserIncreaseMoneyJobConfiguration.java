package com.example.springbatchpractice.job;

import com.example.springbatchpractice.entity.User;
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
 * fileName         : UserIncreaseMoneyJobConfiguration
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : 모든 user의 보유 money를 입력한 값만큼 증가시키는 기능
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
public class UserIncreaseMoneyJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  private final DataSource dataSource;

  private static final int CHUNK_SIZE = 1000;

  @Bean
  Job userIncreaseMoneyJob() throws Exception {
    return jobBuilderFactory.get("userIncreaseMoneyJob")
        .start(userIncreaseMoneyStep())
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  @JobScope
  @Transactional
  public Step userIncreaseMoneyStep()
      throws Exception {
    log.info(">>>>> userIncreaseMoneyStep");
    return stepBuilderFactory.get("userIncreaseMoneyStep")
        .<User, User>chunk(CHUNK_SIZE)
        .reader(userIncreaseMoneyInfoReader())
        .processor(userIncreaseMoneyProcessor(null))
        .writer(userIncreaseMoneyWriter())
        .build();
  }

  @Bean
  @StepScope
  public JdbcPagingItemReader<User> userIncreaseMoneyInfoReader() throws Exception {
    log.info(">>>>> userIncreaseMoneyInfoReader working");

    return new JdbcPagingItemReaderBuilder<User>()
        .pageSize(CHUNK_SIZE)
        .fetchSize(CHUNK_SIZE)
        .dataSource(dataSource)
        .rowMapper(new BeanPropertyRowMapper<>(User.class))
        .queryProvider(selectUserIncreaseMoneyQueryProvider())
        .name("userIncreaseMoneyInfoReader")
        .build();
  }

  @Bean
  public PagingQueryProvider selectUserIncreaseMoneyQueryProvider() throws Exception {
    SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
    queryProvider.setDataSource(dataSource); // Database에 맞는 PagingQueryProvider를 선택하기 위해
    queryProvider.setSelectClause("*");
    queryProvider.setFromClause("from tb_users");

    Map<String, Order> sortKeys = new HashMap<>(1);
    sortKeys.put("id", Order.ASCENDING);

    queryProvider.setSortKeys(sortKeys);

    return queryProvider.getObject();
  }

  @Bean
  @StepScope
  public ItemProcessor<User, User> userIncreaseMoneyProcessor(
      @Value("#{jobParameters[money]}") Integer money) {
    log.info(">>>>> userIncreaseMoneyProcessor working");
    return user -> {
      if (ObjectUtils.isEmpty(user.getDeleteDate())) {
        user.updateMoney(user.getMoney() + money);
        return user;
      }
      return null;
    };
  }

  @Bean
  public JpaItemWriter<User> userIncreaseMoneyWriter() {
    log.info(">>>>> userIncreaseMoneyWriter working");
    JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);

    return jpaItemWriter;
  }
}
