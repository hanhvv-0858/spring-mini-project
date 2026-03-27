package com.example.employeemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class StatisticsResponse {

    // Tổng quan
    private long   totalEmployees;
    private long   totalDepartments;
    private double averageSalary;
    private double totalSalary;
    private double maxSalary;
    private double minSalary;

    // Thống kê theo phòng ban
    private List<DeptStatResponse> departmentStats;

    // Thời điểm tính
    private LocalDateTime calculatedAt;
}
