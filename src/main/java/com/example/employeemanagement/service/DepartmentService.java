package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.request.DepartmentRequest;
import com.example.employeemanagement.dto.response.DepartmentResponse;

import java.util.List;

/**
 * DepartmentService Interface
 */
public interface DepartmentService {
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse createDepartment(DepartmentRequest request);
}
