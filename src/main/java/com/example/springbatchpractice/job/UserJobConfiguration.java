package com.example.springbatchpractice.job;

import com.example.springbatchpractice.dao.UserRepository;
import com.example.springbatchpractice.entity.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class UserJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  private final DataSource dataSource;
  private final UserRepository userRepository;

  private static final int CHUNK_SIZE = 1000;

  @Bean
  public Job userCreateJob() throws Exception {
    return jobBuilderFactory.get("userCreateJob")
        .start(userCreateStep(null))
        .next(userMoneyIncreaseStep(null))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  Job UserMoneyIncreaseJob() throws Exception {
    return jobBuilderFactory.get("userMoneyIncreaseJob")
        .start(userMoneyIncreaseStep(null))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  Job userMoneyGambleJob() throws Exception {
    return jobBuilderFactory.get("userMoneyGambleJob")
        .start(userMoneyGambleStep(null))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  @JobScope
  public Step userCreateStep(@Value("#{jobParameters[user_size]}") Integer user_size) {
    return stepBuilderFactory.get("userCreateStep")
        .tasklet((contribution, chunkContext) -> {
          log.info(">>>>> userCreateStep");

          for (int i = 1; i <= user_size; i++) {
            String name = nameMaker(new Random().nextInt(7));
            Long money = 0L;
            User user = new User(name, money);
            log.info(">>>>> {}st user is {}", i, user.getName());
            userRepository.save(user);
          }

          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  @JobScope
  @Transactional
  public Step userMoneyIncreaseStep(@Value("#{jobParameters[money]}") Integer money)
      throws Exception {
    log.info(">>>>> userMoneyIncreaseStep");
    return stepBuilderFactory.get("userMoneyIncreaseStep")
        .<User, User>chunk(CHUNK_SIZE)
        .reader(userInfoReader(null))
        .processor(increaseUserMoneyProcessor(money))
        .writer(userInfoWriter())
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
  public ItemProcessor<User, User> increaseUserMoneyProcessor(
      @Value("#{jobParameters[money]}") Integer money) {
    log.info(">>>>> increaseUserMoneyProcessor working");
    return user -> {
      User rich_user = new User(user.getId(), user.getName(), user.getMoney());
      rich_user.setMoney(rich_user.getMoney() + money);
      return rich_user;
    };
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

  private String nameMaker(int index) {
    String[] nameArray = {"김철수", "나은지", "이수지", "홍길동", "임꺽정", "이도", "황재연"};

    return index < nameArray.length ? nameArray[index] : "이름 없음";
  }

  private Long gambleMoney(Long money) {
    return (long) (money * ((Math.random() * 1.5) + 0.5));
  }
}
