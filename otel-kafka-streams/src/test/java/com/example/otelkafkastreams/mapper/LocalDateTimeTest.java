package com.example.otelkafkastreams.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LocalDateTimeTest {
    // 方法：LocalDateTime 转纳秒
    private long toUnixNano(LocalDateTime time, ZoneId zone) {
        ZonedDateTime zdt = time.atZone(zone);
        return zdt.toInstant().getEpochSecond() * 1_000_000_000L + zdt.toInstant().getNano();
    }

    @Test
    void testLocalDateTime() {
        // 输入时间
        LocalDateTime inputTime = LocalDateTime.of(2025, 4, 8, 14, 2, 58, 593_000_000);
        ZoneId zone = ZoneId.of("Asia/Shanghai");

        // 转换结果
        long actualNano = toUnixNano(inputTime, zone);

        // 预期值（已知）
        long expectedNano = 1744092178593000000L;

        // 断言一致
        assertEquals(expectedNano, actualNano);
    }
}
