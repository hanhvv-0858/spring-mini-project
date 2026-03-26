package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.request.EmployeeRequest;
import com.example.employeemanagement.dto.response.EmployeeResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.BusinessException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.service.UtilityService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)   // Mặc định: tất cả method là read-only
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UtilityService utilityService;

    // ─── SEED DATA ────────────────────────────────────────────────
    @PostConstruct
    @Transactional   // Override readOnly = true để INSERT được
    public void initSampleData() {
        // Tạo departments trước
        Department engineering = departmentRepository.save(
                Department.builder().name("Engineering").description("Software development").build()
        );
        Department hr = departmentRepository.save(
                Department.builder().name("HR").description("Human resources").build()
        );
        Department finance = departmentRepository.save(
                Department.builder().name("Finance").description("Financial management").build()
        );

        // Tạo employees
        employeeRepository.save(Employee.builder()
                .employeeCode(utilityService.generateEmployeeCode())
                .name("Nguyen Van An").email("an.nguyen@company.com")
                .position("Backend Developer").salary(25_000_000.0)
                .department(engineering).build());

        employeeRepository.save(Employee.builder()
                .employeeCode(utilityService.generateEmployeeCode())
                .name("Tran Thi Bich").email("bich.tran@company.com")
                .position("HR Manager").salary(30_000_000.0)
                .department(hr).build());

        employeeRepository.save(Employee.builder()
                .employeeCode(utilityService.generateEmployeeCode())
                .name("Le Van Cuong").email("cuong.le@company.com")
                .position("Frontend Developer").salary(22_000_000.0)
                .department(engineering).build());

        employeeRepository.save(Employee.builder()
                .employeeCode(utilityService.generateEmployeeCode())
                .name("Pham Thi Dung").email("dung.pham@company.com")
                .position("Accountant").salary(20_000_000.0)
                .department(finance).build());
    }

    // ─── READ ─────────────────────────────────────────────────────
    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── getEmployeeById ──────────────────────────────────────
    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    @Override
    public List<EmployeeResponse> searchByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).toList();
    }

    // Tìm kiếm tổng hợp: theo tên HOẶC phòng ban
    public List<EmployeeResponse> search(String keyword) {
        try {
            List<Employee> results = employeeRepository.searchByKeyword(keyword);
            return results.stream().map(this::toResponse).toList();
        } catch (Exception ex) {
            // DB lỗi, query lỗi... → không lộ chi tiết ra ngoài
            throw new BusinessException("Tìm kiếm thất bại, vui lòng thử lại");
        }
    }

    // ─── createEmployee ───────────────────────────────────────
    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        // Kiểm tra email trùng
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email '" + request.getEmail() + "' đã tồn tại");
        }

        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department", "id", request.getDepartmentId()));

        Employee employee = Employee.builder()
                .employeeCode(utilityService.generateEmployeeCode())
                .name(utilityService.formatName(request.getName()))
                .email(request.getEmail())
                .position(request.getPosition())
                .salary(request.getSalary())
                .department(department)
                .build();

        return toResponse(employeeRepository.save(employee));
    }

    // ─── updateEmployee ───────────────────────────────────────
    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Kiểm tra email trùng với employee KHÁC
        if (request.getEmail() != null &&
                employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new BusinessException("Email '" + request.getEmail() + "' đã tồn tại");
        }

        if (request.getName() != null)
            employee.setName(utilityService.formatName(request.getName()));
        if (request.getEmail() != null)
            employee.setEmail(request.getEmail());
        if (request.getPosition() != null)
            employee.setPosition(request.getPosition());
        if (request.getSalary() != null)
            employee.setSalary(request.getSalary());
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository
                    .findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department", "id", request.getDepartmentId()));
            employee.setDepartment(dept);
        }

        return toResponse(employee);
    }

    // ─── deleteEmployee ───────────────────────────────────────
    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id))
            throw new ResourceNotFoundException("Employee", "id", id);
        employeeRepository.deleteById(id);
    }

    // ─── MAPPER ───────────────────────────────────────────────────
    // Convert Entity → DTO (tránh lộ Entity ra ngoài)
    private EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .employeeCode(e.getEmployeeCode())
                .name(e.getName())
                .email(e.getEmail())
                .position(e.getPosition())
                .salary(e.getSalary())
                .departmentId(e.getDepartment() != null ? e.getDepartment().getId() : null)
                .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : null)
                .createdAt(e.getCreatedAt())
                .build();
    }
}
