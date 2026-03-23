package com.example.employeemanagement.controller;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController           // Mọi method trả về JSON tự động
@RequestMapping("/api")   // Base URL
public class HelloController {

    // GET http://localhost:8080/api/hello
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Employee Management System! 🎉";
    }

    // GET http://localhost:8080/api/info
    // Trả về JSON object
    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
                "app",       "Employee Management System",
                "version",   "1.0.0",
                "timestamp", LocalDateTime.now().toString(),
                "status",    "running"
        );
    }

    // GET http://localhost:8080/api/greet/Alice
    // PathVariable: lấy giá trị từ URL
    @GetMapping("/greet/{name}")
    public Map<String, String> greet(@PathVariable String name) {
        return Map.of(
                "message", "Hello, " + name + "! Welcome to EMS.",
                "name",    name
        );
    }
}