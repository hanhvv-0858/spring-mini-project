package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.DepartmentRequest;
import com.example.employeemanagement.dto.response.DepartmentResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.repository.DepartmentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @GetMapping
    public List<DepartmentResponse> getAll() {
        log.debug("API: GET /api/departments");
        List<DepartmentResponse> result = departmentRepository.findAll()
                .stream()
                .map(d -> DepartmentResponse.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .description(d.getDescription())
                        .employeeCount(d.getEmployees().size())
                        .build())
                .toList();
        log.debug("API: Returned {} departments", result.size());
        return result;
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(
            @Valid @RequestBody DepartmentRequest request) {
        log.info("API: Creating department name='{}'", request.getName());
        Department saved = departmentRepository.save(
                Department.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .build()
        );
        log.info("✅ Department created: id={}, name='{}'",
                saved.getId(), saved.getName());
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