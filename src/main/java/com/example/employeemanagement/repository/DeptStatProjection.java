package com.example.employeemanagement.repository;

/**
 * Interface Projection
 * Spring tự implement interface này khi query trả về
 * Tên method phải khớp với alias trong @Query
 */
public interface DeptStatProjection {
    String getDeptName();
    Long   getTotal();
    Double getAvgSalary();
    Double getMaxSalary();
    Double getMinSalary();
}