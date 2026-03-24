package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.request.EmployeeRequest;
import com.example.employeemanagement.dto.response.EmployeeResponse;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.service.UtilityService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UtilityService utilityService;

    private final Map<Long, EmployeeResponse> store = new LinkedHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // ✅ Chạy SAU KHI utilityService đã được inject
    @PostConstruct
    public void initSampleData() {
        EmployeeRequest emp1 = new EmployeeRequest();
        emp1.setName("Nguyen Van An");
        emp1.setEmail("an.nguyen@company.com");
        emp1.setDepartment("Engineering");
        emp1.setPosition("Backend Developer");
        emp1.setSalary(25_000_000.0);
        createEmployee(emp1);

        EmployeeRequest emp2 = new EmployeeRequest();
        emp2.setName("Tran Thi Bich");
        emp2.setEmail("bich.tran@company.com");
        emp2.setDepartment("HR");
        emp2.setPosition("HR Manager");
        emp2.setSalary(30_000_000.0);
        createEmployee(emp2);

        EmployeeRequest emp3 = new EmployeeRequest();
        emp3.setName("Le Van Cuong");
        emp3.setEmail("cuong.le@company.com");
        emp3.setDepartment("Engineering");
        emp3.setPosition("Frontend Developer");
        emp3.setSalary(22_000_000.0);
        createEmployee(emp3);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return new ArrayList<>(store.values());
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        EmployeeResponse employee = store.get(id);
        if (employee == null) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        return employee;
    }

    @Override
    public List<EmployeeResponse> searchByName(String name) {
        String keyword = name.toLowerCase();
        return store.values().stream()
                .filter(e -> e.getName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Long id = idCounter.getAndIncrement();

        EmployeeResponse response = EmployeeResponse.builder()
                .id(id)
                .employeeCode(utilityService.generateEmployeeCode())
                .name(utilityService.formatName(request.getName()))
                .email(request.getEmail())
                .department(request.getDepartment())
                .position(request.getPosition())
                .salary(request.getSalary())
                .createdAt(LocalDateTime.now())
                .build();

        store.put(id, response);
        return response;
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        EmployeeResponse existing = getEmployeeById(id); // throws nếu không tìm thấy

        // Giữ lại id, employeeCode, createdAt — chỉ cập nhật field được gửi lên
        EmployeeResponse updated = EmployeeResponse.builder()
                .id(existing.getId())
                .employeeCode(existing.getEmployeeCode())
                .createdAt(existing.getCreatedAt())
                .name(request.getName() != null
                        ? utilityService.formatName(request.getName())
                        : existing.getName())
                .email(request.getEmail() != null ? request.getEmail() : existing.getEmail())
                .department(request.getDepartment() != null
                        ? request.getDepartment()
                        : existing.getDepartment())
                .position(request.getPosition() != null
                        ? request.getPosition()
                        : existing.getPosition())
                .salary(request.getSalary() != null ? request.getSalary() : existing.getSalary())
                .build();

        store.put(id, updated);
        return updated;
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!store.containsKey(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        store.remove(id);
    }
}
