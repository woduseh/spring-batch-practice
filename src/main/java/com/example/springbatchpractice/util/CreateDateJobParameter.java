package com.example.springbatchpractice.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

// https://jojoldu.tistory.com/490?category=902551 참고

/**
 * <pre>
 * packageName      : com.example.springbatchpractice.util
 * fileName         : CreateDateJobParameter
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : job parameter로 localdate를 사용하기 위한 유틸리티
 * </pre>
 * ===========================================================
 * <pre>
 * DATE                 AUTHOR                  NOTE
 * -----------------------------------------------------
 * 2022-01-10           JYHwang                 최초 생성
 * </pre>
 */
@Slf4j
@Getter
@NoArgsConstructor
public class CreateDateJobParameter {

  private LocalDate deleteRes;

  @Value("#{jobParameters[deleteRes]}")
  public void setDeleteDate(String deleteRes) {
    this.deleteRes = LocalDate.parse(deleteRes,
        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }
}