package com.example.employeemanagement.service;

import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * MODULE 8 — Report Service với Caching
 *
 * @Cacheable: Lần đầu → chạy method, lưu result vào cache
 *             Lần sau (trong TTL) → trả từ cache, KHÔNG query DB
 *
 * Cache name "reportSummary" → TTL theo config:
 *   dev:  60 giây (application.yml base)
 *   prod: 300 giây (application-prod.yml override)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * LAB 8: Tổng số nhân viên trong hệ thống (có cache)
     *
     * key = "'total'" → cache key cố định (dấu '' trong SpEL = string literal)
     * Mỗi lần gọi trong TTL → trả ngay, không query DB
     */
    @Cacheable(value = "reportSummary", key = "'total'")
    public Map<String, Object> getEmployeeSummary() {
        log.info("📊 Cache MISS — querying DB for employee summary...");

        long totalEmployees = employeeRepository.count();
        long totalDepartments = departmentRepository.count();

        // Tính lương trung bình
        double avgSalary = employeeRepository.findAll()
                .stream()
                .filter(e -> e.getSalary() != null)
                .mapToDouble(e -> e.getSalary())
                .average()
                .orElse(0.0);

        // Tổng lương
        double totalSalary = employeeRepository.findAll()
                .stream()
                .filter(e -> e.getSalary() != null)
                .mapToDouble(e -> e.getSalary())
                .sum();

        Map<String, Object> result = Map.of(
                "totalEmployees",   totalEmployees,
                "totalDepartments", totalDepartments,
                "averageSalary",    Math.round(avgSalary),
                "totalSalary",      Math.round(totalSalary),
                "cachedAt",         java.time.LocalDateTime.now().toString()
        );

        log.info("📊 Summary queried: {} employees, {} depts, avg salary={}",
                totalEmployees, totalDepartments, Math.round(avgSalary));

        return result;
    }

    /**
     * Thống kê số nhân viên theo từng phòng ban (có cache)
     */
    @Cacheable(value = "reportByDept", key = "'all'")
    public List<Map<String, Object>> getEmployeeCountByDepartment() {
        log.info("📊 Cache MISS — querying DB for dept statistics...");

        return departmentRepository.findAll()
                .stream()
                .map(dept -> Map.<String, Object>of(
                        "departmentId",   dept.getId(),
                        "departmentName", dept.getName(),
                        "employeeCount",  dept.getEmployees().size()
                ))
                .toList();
    }
}