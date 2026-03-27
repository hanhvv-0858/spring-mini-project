package com.example.employeemanagement.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MODULE 8 — Scheduled Tasks
 *
 * @Component: Spring quản lý bean này
 * @Scheduled: Spring tự gọi method theo lịch
 *
 * Các method @Scheduled:
 *  - KHÔNG có tham số
 *  - KHÔNG có return value (phải là void)
 *  - Mặc định chạy trên single thread (tất cả task dùng chung 1 thread)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemScheduler {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * LAB 8: Log "System running" mỗi 30 giây
     *
     * fixedRateString: đọc từ config (linh hoạt, không hardcode)
     * Fallback 30000ms nếu property không tồn tại
     */
    @Scheduled(fixedRateString = "${app.scheduler.system-check-interval:30000}")
    public void systemHealthCheck() {
        log.info("💚 System running — {}", LocalDateTime.now().format(FORMATTER));
    }

    /**
     * Dọn dẹp cache lúc 3:00 sáng mỗi ngày
     * Đảm bảo data mới nhất được load vào cache ngày hôm sau
     *
     * Cron: "giây phút giờ ngày tháng thứ"
     *   0 0 3 * * *  → giây=0, phút=0, giờ=3, mỗi ngày
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void midnightCacheReset() {
        log.info("🔄 Midnight cache reset triggered at {}",
                LocalDateTime.now().format(FORMATTER));
        // Cache tự hết hạn theo TTL, log này chỉ để monitor
    }
}