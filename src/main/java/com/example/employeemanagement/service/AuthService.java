package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.auth.AuthResponse;
import com.example.employeemanagement.dto.auth.LoginRequest;
import com.example.employeemanagement.dto.auth.RegisterRequest;
import com.example.employeemanagement.entity.User;
import com.example.employeemanagement.exception.BusinessException;
import com.example.employeemanagement.repository.UserRepository;
import com.example.employeemanagement.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *  Auth Service
 * Xử lý đăng ký và đăng nhập
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtUtil              jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Đăng ký tài khoản mới
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username '" + request.getUsername() + "' đã tồn tại");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Role không hợp lệ. Chỉ chấp nhận: ADMIN, USER");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .build();

        userRepository.save(user);
        log.info("New user registered: username='{}', role={}", user.getUsername(), user.getRole());

        String token = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .expiresIn(jwtExpiration)
                .build();
    }

    /**
     * Đăng nhập — trả về JWT token
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // Spring tự verify username + password qua AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            log.warn("Login failed for username='{}'", request.getUsername());
            throw new BusinessException("Username hoặc password không đúng");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("User không tồn tại"));

        String token = jwtUtil.generateToken(user);
        log.info("User logged in: username='{}', role={}", user.getUsername(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .expiresIn(jwtExpiration)
                .build();
    }
}
