package com.example.employeemanagement.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;

// @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@Data
public class EmployeeRequest {
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Size(max = 100, message = "Chức vụ không quá 100 ký tự")
    private String position;

    @Positive(message = "Lương phải lớn hơn 0")
    private Double salary;

    @NotNull(message = "Phòng ban không được để trống")
    private Long departmentId;
}
