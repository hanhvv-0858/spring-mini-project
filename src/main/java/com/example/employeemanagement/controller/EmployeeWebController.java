package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.EmployeeRequest;
import com.example.employeemanagement.dto.response.DepartmentResponse;
import com.example.employeemanagement.dto.response.EmployeeResponse;
import com.example.employeemanagement.exception.BusinessException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.service.DepartmentService;
import com.example.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeWebController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    // ─── LIST ─────────────────────────────────────────────────────
    @GetMapping("/list")
    public String listEmployees(Model model) {
        log.debug("Web: GET /employees/list");
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("pageTitle", "Danh Sách Nhân Viên");
        return "employees/list";
    }

    // ─── ADD ──────────────────────────────────────────────────────
    @GetMapping("/add")
    public String showAddForm(Model model) {
        log.debug("Web: GET /employees/add — show form");
        model.addAttribute("employeeRequest", new EmployeeRequest());
        model.addAttribute("departments", getDepartments());
        model.addAttribute("pageTitle", "Thêm Nhân Viên Mới");
        model.addAttribute("isEdit", false);
        return "employees/form";
    }

    @PostMapping("/add")
    public String handleAdd(
            @Valid @ModelAttribute EmployeeRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.warn("Web: POST /employees/add — validation failed: {}",
                    bindingResult.getFieldErrors().stream()
                            .map(e -> e.getField() + "=" + e.getDefaultMessage())
                            .toList());
            model.addAttribute("departments", getDepartments());
            model.addAttribute("pageTitle", "Thêm Nhân Viên Mới");
            model.addAttribute("isEdit", false);
            return "employees/form";
        }

        try {
            EmployeeResponse created = employeeService.createEmployee(request);
            log.info("Web: Employee created via form — id={}, name='{}'",
                    created.getId(), created.getName());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Thêm nhân viên '" + created.getName() + "' thành công!");
            return "redirect:/employees/list";
        } catch (BusinessException ex) {
            log.warn("Web: POST /employees/add — business error: {}", ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("departments", getDepartments());
            model.addAttribute("isEdit", false);
            return "employees/form";
        }
    }

    // ─── EDIT ─────────────────────────────────────────────────────
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.debug("Web: GET /employees/edit/{}", id);
        EmployeeResponse employee = employeeService.getEmployeeById(id);

        EmployeeRequest request = new EmployeeRequest();
        request.setName(employee.getName());
        request.setEmail(employee.getEmail());
        request.setPosition(employee.getPosition());
        request.setSalary(employee.getSalary());
        request.setDepartmentId(employee.getDepartmentId());

        model.addAttribute("employeeRequest", request);
        model.addAttribute("employeeId", id);
        model.addAttribute("departments", getDepartments());
        model.addAttribute("pageTitle", "Chỉnh Sửa Nhân Viên");
        model.addAttribute("isEdit", true);
        return "employees/form";
    }

    @PostMapping("/edit/{id}")
    public String handleEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute EmployeeRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.warn("Web: POST /employees/edit/{} — validation failed", id);
            model.addAttribute("employeeId", id);
            model.addAttribute("departments", getDepartments());
            model.addAttribute("pageTitle", "Chỉnh Sửa Nhân Viên");
            model.addAttribute("isEdit", true);
            return "employees/form";
        }

        try {
            EmployeeResponse updated = employeeService.updateEmployee(id, request);
            log.info("Web: Employee updated via form — id={}, name='{}'",
                    updated.getId(), updated.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật nhân viên '" + updated.getName() + "' thành công!");
            return "redirect:/employees/list";
        } catch (BusinessException ex) {
            log.warn("Web: POST /employees/edit/{} — business error: {}", id, ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("employeeId", id);
            model.addAttribute("departments", getDepartments());
            model.addAttribute("isEdit", true);
            return "employees/form";
        }
    }

    // ─── DELETE ───────────────────────────────────────────────────
    @PostMapping("/delete/{id}")
    public String handleDelete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            log.info("Web: Employee deleted via form — id={}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa nhân viên thành công!");
        } catch (Exception ex) {
            log.warn("Web: Delete employee id={} failed: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa nhân viên: " + ex.getMessage());
        }
        return "redirect:/employees/list";
    }

    // ─── SEARCH ───────────────────────────────────────────────────
    @GetMapping("/search")
    public String searchEmployees(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Tìm Kiếm Nhân Viên");

        if (keyword != null && keyword.trim().length() >= 2) {
            log.debug("Web: Search employees keyword='{}'", keyword.trim());
            List<EmployeeResponse> results = employeeService.search(keyword.trim());
            model.addAttribute("employees", results);
            model.addAttribute("resultCount", results.size());
            log.debug("Web: Search returned {} results", results.size());
        } else if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("searchError", "Từ khóa phải có ít nhất 2 ký tự");
        }

        return "employees/search";
    }

    // ─── HELPER ───────────────────────────────────────────────────
    private List<DepartmentResponse> getDepartments() {
        return departmentService.getAllDepartments();  // ← Qua Service, đúng layer
    }
}