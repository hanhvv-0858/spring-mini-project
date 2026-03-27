package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.EmployeeRequest;
import com.example.employeemanagement.dto.response.EmployeeResponse;
import com.example.employeemanagement.exception.BusinessException;
import com.example.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // GET /api/employees
    // GET: Cả USER và ADMIN đều xem được
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
        // ① Kiểm tra keyword null hoặc rỗng
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException("Từ khóa tìm kiếm không được để trống");
        }
        // ② Kiểm tra độ dài tối thiểu
        if (keyword.trim().length() < 2) {
            throw new BusinessException("Từ khóa tìm kiếm phải có ít nhất 2 ký tự");
        }
        List<EmployeeResponse> results = employeeService.search(keyword.trim());
        return ResponseEntity.ok(results);
    }

    // POST, PUT, DELETE: Chỉ ADMIN mới được phép thao tác
    // POST /api/employees
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {  // ← thêm @Valid
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(request));
    }

    // PUT /api/employees/1
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {  // ← thêm @Valid
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    // DELETE /api/employees/1
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}