package com.example.employeemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentResponse {
    private Long id;
    private String name;
    private String description;
    private long employeeCount;   // Số nhân viên trong phòng ban
}