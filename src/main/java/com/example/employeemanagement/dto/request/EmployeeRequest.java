package com.example.employeemanagement.dto.request;
import lombok.Data;

// @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@Data
public class EmployeeRequest {
    private String name;
    private String email;
    private String position;
    private Double salary;
    private Long departmentId;
}
