package com.example.employeemanagement.controller;

import com.example.employeemanagement.service.UtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController           // Mọi method trả về JSON tự động
@RequestMapping("/api")   // Base URL
@RequiredArgsConstructor  // Lombok tự tạo constructor inject 2 field final bên dưới
public class HelloController {

    // ✅ Constructor Injection — Spring tự inject, không cần @Autowired tường minh
    private final UtilityService utilityService;
    private final PasswordEncoder passwordEncoder;  // Bean từ A

    // GET http://localhost:8080/api/hello
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of(
                "message", "Employee Management System is running!",
                "sampleEmployeeCode", utilityService.generateEmployeeCode(),
                "formattedName", utilityService.formatName("  john   doe  ")
        );
    }
    // GET "http://localhost:8080/api/demo/hash?password=mySecret123"
    @GetMapping("/demo/hash")
    public Map<String, String> demoHash(@RequestParam String password) {
        String hashed = passwordEncoder.encode(password);
        return Map.of(
                "original", password,
                "hashed", hashed,
                "matches", String.valueOf(passwordEncoder.matches(password, hashed))
        );
    }

}