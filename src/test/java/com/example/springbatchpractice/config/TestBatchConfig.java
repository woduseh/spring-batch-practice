package com.example.springbatchpractice.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 참고: https://velog.io/@miz/SpringBatch-Jpa-Test-%EC%84%A4%EC%A0%95
 *
 * @author JYHwang
 */


@Configuration
// @Compoent 어노테이션이 붙어있는 class를 빈으로 등록
// ex ) @Compoent, @Configuration, @Repository, @Service, @Controller, @RestController
@EnableAutoConfiguration
// 스프링부트의 meta 파일을 읽어 미리 정의되어 있는 자바 설정 파일(@Configuration)들을 빈으로 등록하는 역할
@EnableBatchProcessing
// JPA 사용을 위한 어노테이션들
@EntityScan("com.example.springbatchpractice")
@EnableJpaRepositories("com.example.springbatchpractice")
@EnableTransactionManagement
@ActiveProfiles("local")
public class TestBatchConfig {

}