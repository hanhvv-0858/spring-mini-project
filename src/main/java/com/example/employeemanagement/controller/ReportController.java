package com.example.employeemanagement.controller;

import com.example.employeemanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MODULE 8 — Report API
 *
 * GET /api/reports/summary          → Tổng hợp toàn hệ thống (cache 1-5 phút)
 * GET /api/reports/by-department    → Thống kê theo phòng ban (cache 1-5 phút)
 */
@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * LAB 8: API thống kê tổng số nhân viên (có caching)
     *
     * Lần đầu gọi   → ReportService query DB → 200ms
     * Lần 2..N (trong TTL) → trả từ cache    → 1ms
     * Sau TTL        → Cache expired → query DB lại
     *
     * Quan sát log để thấy sự khác biệt:
     *   Cache MISS: "📊 Cache MISS — querying DB for employee summary..."
     *   Cache HIT:  (không có log → method không được gọi)
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        log.debug("API: GET /api/reports/summary");
        return ResponseEntity.ok(reportService.getEmployeeSummary());
    }

    /**
     * Thống kê số nhân viên theo phòng ban
     */
    @GetMapping("/by-department")
    public ResponseEntity<List<Map<String, Object>>> getByDepartment() {
        log.debug("API: GET /api/reports/by-department");
        return ResponseEntity.ok(reportService.getEmployeeCountByDepartment());
    }
}