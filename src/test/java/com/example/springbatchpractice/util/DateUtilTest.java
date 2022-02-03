package com.example.springbatchpractice.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DateUtilTest {

  @Test
  @DisplayName("랜덤 일자 생성")
  void randomTimeMaker() {
    long minDay = LocalDate.now().toEpochDay();
    long maxDay = LocalDate.of(2022, 12, 31).toEpochDay();
    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
    LocalDate randomDate = LocalDate.ofEpochDay(randomDay);

    assertThat(randomDate).isAfter(LocalDate.ofEpochDay(minDay));
    assertThat(randomDate).isBefore(LocalDate.ofEpochDay(maxDay));
  }
}
