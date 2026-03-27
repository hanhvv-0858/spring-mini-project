package com.example.employeemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DeptStatResponse {
    private String deptName;
    private long   employeeCount;
    private double avgSalary;
    private double maxSalary;
    private double minSalary;
    private double percentage;     // % số NV so với tổng
}
