package com.example.employeemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartmentRequest {

    @NotBlank(message = "Tên phòng ban không được để trống")
    @Size(min = 2, max = 100, message = "Tên phòng ban phải từ 2 đến 100 ký tự")
    private String name;

    @Size(max = 255, message = "Mô tả không quá 255 ký tự")
    private String description;
}
