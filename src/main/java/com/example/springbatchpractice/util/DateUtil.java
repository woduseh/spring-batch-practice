package com.example.springbatchpractice.util;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class DateUtil {

  private DateUtil() {

  }

  public static LocalDate randomTimeMaker() {
    long minDay = LocalDate.now().toEpochDay();
    long maxDay = LocalDate.of(2022, 2, 28).toEpochDay();
    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);

    return LocalDate.ofEpochDay(randomDay);
  }
}
