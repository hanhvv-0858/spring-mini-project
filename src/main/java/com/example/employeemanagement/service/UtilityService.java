package com.example.employeemanagement.service;

import com.example.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UtilityService {
    private final EmployeeRepository employeeRepository;

    /**
     * Khởi tạo lazy — chỉ query DB lần đầu gọi generateEmployeeCode()
     * Tránh circular dependency với EmployeeServiceImpl
     */
    private AtomicInteger counter = null;

    /**
     * Lấy counter hiện tại — khởi tạo từ DB nếu chưa có
     *
     * Với H2:   DB trống mỗi lần restart → bắt đầu từ 1000
     * Với MySQL: DB có data → tìm số thứ tự lớn nhất đang dùng → +1
     */
    private synchronized AtomicInteger getCounter() {
        if (counter == null) {
            int maxSeq = employeeRepository.findAll()
                    .stream()
                    .map(e -> {
                        try {
                            // Parse số thứ tự từ code: "EMP-20261003" → 1003
                            String code = e.getEmployeeCode();
                            if (code == null) return 0;
                            String[] parts = code.split("-");
                            // parts[1] = "20261003" → lấy 4 số cuối
                            String seq = parts[1].substring(4); // bỏ 4 số năm
                            return Integer.parseInt(seq);
                        } catch (Exception ex) {
                            return 0;
                        }
                    })
                    .max(Integer::compareTo)
                    .orElse(999); // Chưa có employee nào → bắt đầu từ 1000

            counter = new AtomicInteger(maxSeq + 1);
            log.info("UtilityService: Employee code counter initialized at {}", maxSeq + 1);
        }
        return counter;
    }

    /**
     * Tạo mã nhân viên: EMP-{YEAR}{SEQ}
     * VD: EMP-20261004, EMP-20261005, ...
     */
    public String generateEmployeeCode() {
        int year = LocalDate.now().getYear();
        int seq = getCounter().getAndIncrement();
        return String.format("EMP-%d%04d", year, seq);
    }

    /**
     * Format tên: "vũ văn hạnh" → "Vũ Văn Hạnh"
     */
    public String formatName(String name) {
        if (name == null || name.isBlank()) return "";
        return Arrays.stream(name.trim().split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0))
                        + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
