package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.auth.AuthResponse;
import com.example.employeemanagement.dto.auth.LoginRequest;
import com.example.employeemanagement.dto.auth.RegisterRequest;
import com.example.employeemanagement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register request for username='{}'", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Login request for username='{}'", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }
}