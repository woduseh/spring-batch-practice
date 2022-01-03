# spring-batch-practice

Spring Batch 연습

## What is it?

대용량 데이터를 Spring Batch로 처리하기 위한 연습용 프로젝트

## Function

1. UserCreateJob: jobParameters로 만들고 싶은 유저 숫자를 입력 그만큼 user를 만들어서 DB에 저장하는 작업

2. UserMoneyIncreaseJob: jobParameters로 유저에게 지급하고 싶은 금액을 입력 모든 user의 money 값을 해당 금액만큼 증가시킴

3. UserMoneyRandomJob: jobParameters로 기준 금액을 입력 기준 금액보다 많은 돈을 보유한 user의 money 값을 현재 값의 0.5 ~ 2.0배
   사이로 랜덤하게 변경하는 작업

## Usage

Docker로 사용할 경우,

1. Build : $ docker build -t batchdocker:test .
2. Run : $ docker run -e 'job.name=userCreateJob user_size=100 money=10000' -d batchdocker:test

위와 같은 식으로 사용할 수 있음

(현재 Docker component로 DB를 추가하지 않아 userCreateJob은 H2로 실행되고 즉시 종료되기에 결과를 확인할 수 없음.)

### TODO

실행 중 발생한 warning 2가지 해결

1. JPA does not support custom isolation levels, so locks may not be taken when launching Jobs
2. org.springframework.batch.item.ItemProcessor is an interface. The implementing class will not be
   queried for annotation based listener configurations. If using @StepScope on a @Bean method, be
   sure to return the implementing class so listener annotations can be used.

셋 다 성공하면 Quartz를 추가해서 정해진 시간에 실행할 수 있도록

Quartz도 성공하면 JobParameter를 runtime argument로 주는 대신 더 편하게 줄 수 있도록 (실제 TMS처럼)
