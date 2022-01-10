package com.example.springbatchpractice.job;

import com.example.springbatchpractice.config.TestBatchConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * <pre>
 * packageName      : com.example.springbatchpractice.job
 * fileName         : UserNoticeDeleteResJobTest
 * author           : JYHwang
 * date             : 2022-01-10
 * description      : UserNoticeDeleteResJob의 작동 확인을 위한 테스트 코드
 * </pre>
 * ===========================================================
 * <pre>
 * DATE                 AUTHOR                  NOTE
 * -----------------------------------------------------
 * 2022-01-10           JYHwang                 최초 생성
 * </pre>
 */

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(classes = {TestBatchConfig.class, UserNoticeDeleteResJobConfiguration.class})
public class UserNoticeDeleteResJobTest {

}
