package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.request.DepartmentRequest;
import com.example.employeemanagement.dto.response.DepartmentResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.exception.BusinessException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic của Department nằm ở đây, không để trong Controller.
 * Bao gồm:
 *  - Validate tên phòng ban trùng trước khi tạo
 *  - Mapping Entity → DTO
 *  - Transaction management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        log.debug("Fetching all departments with employee count");

        // Dùng JOIN FETCH để tránh N+1
        return departmentRepository.findAllWithEmployees()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        log.info("Creating department name='{}'", request.getName());

        // Business rule: tên phòng ban không được trùng
        if (departmentRepository.existsByNameIgnoreCase(request.getName())) {
            log.warn("Create failed — duplicate department name: '{}'", request.getName());
            throw new BusinessException("Phòng ban '" + request.getName() + "' đã tồn tại");
        }

        Department saved = departmentRepository.save(
                Department.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .build()
        );

        log.info("✅ Department created: id={}, name='{}'", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    // ── Mapper ────────────────────────────────────────────────────
    private DepartmentResponse toResponse(Department d) {
        return DepartmentResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .employeeCount(d.getEmployees().size())
                .build();
    }
}
