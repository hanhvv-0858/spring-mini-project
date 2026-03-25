package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.EmployeeRequest;
import com.example.employeemanagement.dto.response.EmployeeResponse;
import com.example.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // GET /api/employees
    // GET /api/employees?department=Engineering
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false) String department) {

        List<EmployeeResponse> employees = employeeService.getAllEmployees();

        if (department != null && !department.isBlank()) {
            employees = employees.stream()
                    .filter(e -> e.getDepartmentName() != null &&   // ← fix Module 4
                            e.getDepartmentName().equalsIgnoreCase(department))
                    .toList();
        }

        return ResponseEntity.ok(employees);
    }

    // GET /api/employees/1
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // GET /api/employees/search?keyword=nguyen
    // Tìm theo tên HOẶC phòng ban (dùng @Query JPQL)
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponse>> search(
            @RequestParam String keyword) {
        return ResponseEntity.ok(employeeService.search(keyword));
    }

    // POST /api/employees
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {  // ← thêm @Valid
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(request));
    }

    // PUT /api/employees/1
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {  // ← thêm @Valid
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    // DELETE /api/employees/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}