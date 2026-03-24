package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.EmployeeRequest;
import com.example.employeemanagement.dto.response.EmployeeResponse;
import com.example.employeemanagement.service.EmployeeService;
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

    // ─── GET ALL ──────────────────────────────────────────────────
    // GET /api/employees
    // GET /api/employees?department=Engineering  ← filter tuỳ chọn
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false) String department) {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        // Filter theo department nếu có query param
        if (department != null && !department.isBlank()) {
            employees = employees.stream()
                    .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                    .toList();  // Java 16+ — thay .collect(Collectors.toList()) cho gọn
        }

        return ResponseEntity.ok(employees);  // 200 OK
    }

    // ─── GET BY ID ────────────────────────────────────────────────
    // GET /api/employees/1
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // ─── SEARCH ───────────────────────────────────────────────────
    // GET /api/employees/search?name=nguyen
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(
            @RequestParam String name) {
        return ResponseEntity.ok(employeeService.searchByName(name));
    }

    // ─── CREATE ───────────────────────────────────────────────────
    // POST /api/employees
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @RequestBody EmployeeRequest request) {
        EmployeeResponse created = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // 201 Created
    }

    // ─── UPDATE ───────────────────────────────────────────────────
    // PUT /api/employees/1
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    // ─── DELETE ───────────────────────────────────────────────────
    // DELETE /api/employees/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}