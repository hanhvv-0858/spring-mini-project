package com.example.employeemanagement.exception;

import com.example.employeemanagement.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j  // Lombok tạo logger: log.error(), log.warn()...
@RestControllerAdvice  // = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {

    // ─── 1. Validation Error (400) ────────────────────────────────
    // Xảy ra khi @Valid fail trên @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Thu thập tất cả field lỗi
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(fieldName, message);
        });

        log.warn("Validation failed for request {}: {}", request.getRequestURI(), fieldErrors);

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Failed")
                        .message("Dữ liệu không hợp lệ")
                        .path(request.getRequestURI())
                        .errors(fieldErrors)  // Map field → message lỗi
                        .build()
        );
    }

    // ─── 2. Resource Not Found (404) ──────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Not Found")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    // ─── 3. Business Logic Error (400) ────────────────────────────
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessError(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn("Business error: {}", ex.getMessage());

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Bad Request")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    // ─── 4. Catch-all (500) ───────────────────────────────────────
    // Bắt tất cả exception không được handle ở trên
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        // Log đầy đủ stacktrace để debug
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("Internal Server Error")
                        .message("Đã xảy ra lỗi, vui lòng thử lại sau")
                        // Không lộ ex.getMessage() — ẩn thông tin nội bộ với client
                        .path(request.getRequestURI())
                        .build()
        );
    }
}
