FROM openjdk:8-jre-alpine
MAINTAINER JYHwang
ENV APP_HOME=/spring-batch-practice/

WORKDIR $APP_HOME

COPY build/libs/*.jar application.jar

CMD ["java", "-jar", "application.jar"]

