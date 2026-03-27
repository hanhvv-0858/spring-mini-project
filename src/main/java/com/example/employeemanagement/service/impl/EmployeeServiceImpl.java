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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import com.example.employeemanagement.entity.User;
import com.example.employeemanagement.repository.UserRepository;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UtilityService utilityService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Đọc từ application-prod.yml: spring.seed-data.enabled
     * dev  → true  (H2 reset mỗi lần restart, luôn phải seed)
     * prod → true  (lần đầu), false (sau khi đã có data)
     * Mặc định true nếu property không tồn tại
     */
    @Value("${spring.seed-data.enabled:true}")
    private boolean seedDataEnabled;

    // ─── SEED DATA ────────────────────────────────────────────────
    @PostConstruct
    @Transactional
    public void initSampleData() {
        if (!seedDataEnabled) {
            log.info("Seed data disabled by config, skipping...");
            return;
        }

        // MySQL giữ data qua restart → chỉ seed khi DB đang trống
        if (departmentRepository.count() > 0) {
            log.info("Database already has data ({} departments), skipping seed.",
                    departmentRepository.count());
            return;
        }

        log.info("Starting database seed...");

        Department engineering = departmentRepository.save(
                Department.builder().name("Engineering").description("Software development").build());
        Department hr = departmentRepository.save(
                Department.builder().name("HR").description("Human resources").build());
        Department finance = departmentRepository.save(
                Department.builder().name("Finance").description("Financial management").build());

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

        log.info("✅ Seed completed: {} departments, {} employees",
                departmentRepository.count(), employeeRepository.count());

        // Seed default users (chỉ khi chưa có)
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(User.Role.ADMIN)
                    .build());

            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .role(User.Role.USER)
                    .build());

            log.info("Default users created: admin/admin123, user/user123");
        }
    }

    // ─── getAllEmployees ───────────────────────────────────────
    @Override
    public List<EmployeeResponse> getAllEmployees() {
        log.debug("Fetching all employees");
        List<EmployeeResponse> result = employeeRepository.findAll()
                .stream().map(this::toResponse).toList();
        log.debug("Found {} employees", result.size());
        return result;
    }

    // ─── getEmployeeById ──────────────────────────────────────
    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        log.debug("Fetching employee id={}", id);
        return employeeRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("Employee not found: id={}", id);
                    return new ResourceNotFoundException("Employee", "id", id);
                });
    }

    @Override
    public List<EmployeeResponse> searchByName(String name) {
        log.debug("Searching employees by name='{}'", name);
        return employeeRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<EmployeeResponse> search(String keyword) {
        log.debug("Searching employees by keyword='{}'", keyword);
        try {
            List<EmployeeResponse> results = employeeRepository.searchByKeyword(keyword)
                    .stream().map(this::toResponse).toList();
            log.debug("Search '{}' returned {} results", keyword, results.size());
            return results;
        } catch (Exception ex) {
            log.error("Search failed for keyword='{}': {}", keyword, ex.getMessage(), ex);
            throw new BusinessException("Tìm kiếm thất bại, vui lòng thử lại");
        }
    }

    // ─── createEmployee ───────────────────────────────────────
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reportSummary", allEntries = true),
            @CacheEvict(value = "reportByDept",  allEntries = true)
    })
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating employee: name='{}', email='{}'",
                request.getName(), request.getEmail());

        if (employeeRepository.existsByEmail(request.getEmail())) {
            log.warn("Create failed — duplicate email: '{}'", request.getEmail());
            throw new BusinessException("Email '" + request.getEmail() + "' đã tồn tại");
        }

        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() -> {
                    log.warn("Create failed — department not found: id={}", request.getDepartmentId());
                    return new ResourceNotFoundException("Department", "id", request.getDepartmentId());
                });

        Employee employee = Employee.builder()
                .employeeCode(utilityService.generateEmployeeCode())
                .name(utilityService.formatName(request.getName()))
                .email(request.getEmail())
                .position(request.getPosition())
                .salary(request.getSalary())
                .department(department)
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("✅ Employee created: id={}, code='{}', name='{}', dept='{}'",
                saved.getId(), saved.getEmployeeCode(), saved.getName(), department.getName());

        return toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reportSummary", allEntries = true),
            @CacheEvict(value = "reportByDept",  allEntries = true)
    })
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        log.info("Updating employee id={}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed — employee not found: id={}", id);
                    return new ResourceNotFoundException("Employee", "id", id);
                });

        if (request.getEmail() != null &&
                employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            log.warn("Update failed — duplicate email: '{}' for id={}", request.getEmail(), id);
            throw new BusinessException("Email '" + request.getEmail() + "' đã tồn tại");
        }

        // Log các field đang thay đổi
        StringBuilder changes = new StringBuilder();

        if (request.getName() != null) {
            String formatted = utilityService.formatName(request.getName());
            changes.append("name='").append(formatted).append("' ");
            employee.setName(formatted);
        }
        if (request.getEmail() != null) {
            changes.append("email='").append(request.getEmail()).append("' ");
            employee.setEmail(request.getEmail());
        }
        if (request.getPosition() != null) {
            changes.append("position='").append(request.getPosition()).append("' ");
            employee.setPosition(request.getPosition());
        }
        if (request.getSalary() != null) {
            changes.append("salary=").append(request.getSalary()).append(" ");
            employee.setSalary(request.getSalary());
        }
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository
                    .findById(request.getDepartmentId())
                    .orElseThrow(() -> {
                        log.warn("Update failed — department not found: id={}", request.getDepartmentId());
                        return new ResourceNotFoundException("Department", "id", request.getDepartmentId());
                    });
            changes.append("department='").append(dept.getName()).append("' ");
            employee.setDepartment(dept);
        }

        log.info("✅ Employee updated: id={}, changes=[{}]", id, changes.toString().trim());
        return toResponse(employee);
    }

    // ─── deleteEmployee ───────────────────────────────────────
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reportSummary", allEntries = true),
            @CacheEvict(value = "reportByDept",  allEntries = true)
    })
    public void deleteEmployee(Long id) {
        log.info("Deleting employee id={}", id);

        if (!employeeRepository.existsById(id)) {
            log.warn("Delete failed — employee not found: id={}", id);
            throw new ResourceNotFoundException("Employee", "id", id);
        }

        employeeRepository.deleteById(id);
        log.info("✅ Employee deleted: id={}", id);
    }

    // ─── MAPPER ───────────────────────────────────────────────────
    // Convert Entity → DTO, có thể dùng MapStruct nếu phức tạp hơn
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