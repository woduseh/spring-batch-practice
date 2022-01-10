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

@Slf4j
@RequiredArgsConstructor
@Configuration
public class UserGambleJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  private final DataSource dataSource;

  private static final int CHUNK_SIZE = 1000;

  @Bean
  Job userMoneyGambleJob() throws Exception {
    return jobBuilderFactory.get("userMoneyGambleJob")
        .start(userMoneyGambleStep(null))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  @JobScope
  @Transactional
  public Step userMoneyGambleStep(@Value("#{jobParameters[base_amount]}") Integer base_amount)
      throws Exception {
    log.info(">>>>> userMoneyGambleStep");
    return stepBuilderFactory.get("userMoneyGambleStep")
        .<User, User>chunk(CHUNK_SIZE)
        .reader(userInfoReader(null))
        .processor(gambleUserMoneyProcessor(base_amount))
        .writer(userInfoWriter())
        .build();
  }

  @Bean
  @StepScope
  public JdbcPagingItemReader<User> userInfoReader(
      @Value("#{jobParameters[base_amount]}") Integer base_amount) throws Exception {
    log.info(">>>>> userInfoReader working");

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("base_amount", base_amount);

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
    queryProvider.setWhereClause("where money >= :base_amount");

    Map<String, Order> sortKeys = new HashMap<>(1);
    sortKeys.put("id", Order.ASCENDING);

    queryProvider.setSortKeys(sortKeys);

    return queryProvider.getObject();
  }

  @Bean
  @StepScope
  public ItemProcessor<User, User> gambleUserMoneyProcessor(
      @Value("#{jobParameters[base_amount]}") Integer base_amount) {
    log.info(">>>>> gambleUserMoneyProcessor working");
    return user -> {
      User gamble_user = new User(user.getId(), user.getName(), user.getMoney());
      if (user.getMoney() >= base_amount) {
        gamble_user.setMoney(gambleMoney(gamble_user.getMoney()));
      }
      return gamble_user;
    };
  }

  @Bean
  public JpaItemWriter<User> userInfoWriter() {
    log.info(">>>>> userInfoWriter working");
    JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);

    return jpaItemWriter;
  }

  private Long gambleMoney(Long money) {
    return (long) (money * ((Math.random() * 1.5) + 0.5));
  }
}
