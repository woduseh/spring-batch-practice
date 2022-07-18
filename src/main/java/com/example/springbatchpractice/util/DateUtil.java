package com.example.springbatchpractice.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.random.RandomGenerator;

public class DateUtil {

  private DateUtil() {

  }

  public static LocalDate randomTimeMaker() {
    LocalDate now = LocalDate.now();
    long minDay = now.toEpochDay();
    long maxDay = now.plus(1, ChronoUnit.MONTHS).toEpochDay();
    long randomDay = RandomGenerator.getDefault().nextLong(minDay, maxDay);

    return LocalDate.ofEpochDay(randomDay);
  }
}
