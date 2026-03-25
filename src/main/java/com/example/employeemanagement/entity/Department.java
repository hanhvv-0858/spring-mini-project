package com.example.employeemanagement.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    // mappedBy = tên field "department" trong class Employee
    // cascade = thao tác trên Department tự lan sang Employee
    // orphanRemoval = xóa Employee nếu bị remove khỏi list này
    @OneToMany(mappedBy = "department",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();
}