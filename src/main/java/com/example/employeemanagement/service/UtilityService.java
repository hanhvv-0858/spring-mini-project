package com.example.employeemanagement.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class UtilityService {
    // AtomicInteger đảm bảo thread-safe khi generate code đồng thời
    private final AtomicInteger counter = new AtomicInteger(1000);

    /**
     * Tạo mã nhân viên tự động: EMP-20250001
     * Format: EMP-{YEAR}{4 chữ số tăng dần}
     */
    public String generateEmployeeCode() {
        int year = LocalDate.now().getYear();
        int seq = counter.getAndIncrement();
        return String.format("EMP-%d%04d", year, seq);
        // EMP-20251001, EMP-20251002, ...
    }

    /**
     * Chuẩn hóa tên: "  john   doe  " → "John Doe"
     */
    public String formatName(String name) {
        if (name == null || name.isBlank()) return "";
        // Split theo whitespace, capitalize từng từ, join lại
        return java.util.Arrays.stream(name.trim().split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0))
                        + word.substring(1).toLowerCase())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    /**
     * Format ngày theo pattern đẹp hơn cho UI
     */
    public String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
