# spring-batch-practice

Spring Batch 연습

## What is it?

대용량 데이터를 Spring Batch로 처리하기 위한 연습용 프로젝트

## Function

1. UserCreateJob
    - 사용법: jobParameters로 만들고 싶은 유저 숫자를 입력
    - 기능: 그만큼 user를 만들어서 DB에 저장하는 작업

2. UserMoneyIncreaseJob
    - 사용법: jobParameters로 유저에게 지급하고 싶은 금액을 입력
    - 기능: 모든 user의 money 값을 해당 금액만큼 증가시킴

3. UserMoneyRandomJob
    - 사용법: jobParameters로 기준 금액을 입력
    - 기능 기준 금액보다 많은 돈을 보유한 user의 money 값을 현재 값의 0.5 ~ 2.0배 사이로 랜덤하게 변경하는 작업

## Usage

갱신 예정

~~Docker로 사용할 경우,~~

~~1. Build : $ docker build -t batchdocker:test .~~
~~2. Run : $ docker run -e 'job.name=userCreateJob user_size=100 money=10000' -d batchdocker:test~~

~~위와 같은 식으로 사용할 수 있음~~

~~(현재 Docker component로 DB를 추가하지 않아 userCreateJob은 H2로 실행되고 즉시 종료되기에 결과를 확인할 수 없음.)~~

## TODO

- User Entity에 삭제 예정일, 삭제일 Column을 추가
- 현재 날짜와 삭제 예정일을 비교
    - 삭제 예정일 7일, 3일, 1일 전이면 log로 경고 문구를 출력
    - 삭제 예정일 당일일 경우 삭제일을 갱신
- 추후 모든 다른 기능에서 삭제일 값이 존재하는 User를 사용하지 않음