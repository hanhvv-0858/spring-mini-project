package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.DepartmentRequest;
import com.example.employeemanagement.dto.response.DepartmentResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @GetMapping
    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll()
                .stream()
                .map(d -> DepartmentResponse.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .description(d.getDescription())
                        .employeeCount(d.getEmployees().size())
                        .build())
                .toList();
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(
            @RequestBody DepartmentRequest request) {
        Department saved = departmentRepository.save(
                Department.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                DepartmentResponse.builder()
                        .id(saved.getId())
                        .name(saved.getName())
                        .description(saved.getDescription())
                        .employeeCount(0)
                        .build()
        );
    }
}