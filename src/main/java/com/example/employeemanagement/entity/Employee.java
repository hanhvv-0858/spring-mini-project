package com.example.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String employeeCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 100)
    private String position;

    @Column
    private Double salary;

    @Column(updatable = false)   // Không cho phép UPDATE column này
    private LocalDateTime createdAt;

    // Quan hệ N-1: nhiều Employee thuộc 1 Department
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")  // FK column trong table employees
    private Department department;

    // Tự set createdAt trước khi INSERT (không cần gán thủ công)
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}