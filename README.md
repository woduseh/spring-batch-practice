# spring-batch-practice

Spring Batch 연습용 프로젝트

## What is it?

대용량 데이터를 Spring Batch로 처리하기 위한 연습용 프로젝트

100만건 이상 데이터를 생성, 사용 시 하나하나의 job의 실행시간이 5분까지 늘어남.

## Function

1. UserCreateJob
    - 사용법: jobParameters로 만들고 싶은 유저 숫자, 모든 유저에게 지급할 기본금를 입력
    - 기능: 그만큼 user를 만들어서 DB에 저장하는 작업


2. UserMoneyIncreaseJob
    - 사용법: jobParameters로 유저에게 지급하고 싶은 금액을 입력
    - 기능: 모든 user의 money 값을 해당 금액만큼 증가시킴


3. UserGambleJob
    - 사용법: jobParameters로 기준 금액을 입력
    - 기능: 기준 금액보다 많은 돈을 보유한 user의 money 값을 현재 값의 0.5 ~ 2.0배 사이로 랜덤하게 변경하는 작업


4. UserDeleteJob
    - 기능: 삭제 예정일 당일이 된 사용자를 삭제 (논리 삭제)하는 기능


5. UserNoticeDeleteResJob
    - 사용법: dueDate를 입력하면 삭제 예정일과 현재 날짜의 차이가 입력한 dueDate 이하인 모든 사용자에게 계정이 삭제될 예정이라 경고

## Usage

1. UserJobScheduler에서 job parameter의 값과 trigger의 crontab을 원하는 값으로 수정
2. Docker로 배포하여 사용

## TODO

- Docker에 mysql 서버 추가하여 배포하기