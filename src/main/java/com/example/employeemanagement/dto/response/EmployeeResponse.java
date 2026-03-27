package com.example.employeemanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder  // Dùng Builder pattern thay vì setter dài dòng
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String name;
    private String email;
    private String position;
    private Double salary;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
}