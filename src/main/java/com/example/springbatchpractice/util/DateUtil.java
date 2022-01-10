package com.example.springbatchpractice.util;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class DateUtil {

  public static LocalDate randomTimeMaker() {
    long minDay = LocalDate.of(2022, 1, 7).toEpochDay();
    long maxDay = LocalDate.of(2022, 12, 31).toEpochDay();
    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);

    return LocalDate.ofEpochDay(randomDay);
  }
}
