package com.example.employeemanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder  // Dùng Builder pattern thay vì setter dài dòng
public class EmployeeResponse {
    private Long id;
    private String employeeCode;    // EMP-20251001 (từ UtilityService)
    private String name;
    private String email;
    private String department;
    private String position;
    private Double salary;
    private LocalDateTime createdAt;
}