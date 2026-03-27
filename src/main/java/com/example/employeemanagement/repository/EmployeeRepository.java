package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Tìm theo tên (không phân biệt hoa thường)
    List<Employee> findByNameContainingIgnoreCase(String name);

    // Tìm theo department ID
    List<Employee> findByDepartmentId(Long departmentId);

    // Tìm theo tên department
    List<Employee> findByDepartmentNameIgnoreCase(String departmentName);

    // Kiểm tra email tồn tại chưa
    boolean existsByEmail(String email);

    // Kiểm tra email tồn tại nhưng loại trừ 1 employee (dùng khi update)
    boolean existsByEmailAndIdNot(String email, Long id);

    // JPQL — tìm theo tên HOẶC department (dùng cho search tổng hợp)
    @Query("""
        SELECT e FROM Employee e
        JOIN FETCH e.department d
        WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<Employee> searchByKeyword(@Param("keyword") String keyword);

    Optional<Employee> findByEmail(String email);

    // ── Thống kê ────────────────────────────────────────
    /**
     * Thống kê theo phòng ban: số NV, lương TB, max, min
     * Dùng Projection để chỉ lấy đúng field cần (tối ưu hơn load Entity)
     */
    @Query("""
        SELECT d.name        AS deptName,
               COUNT(e.id)   AS total,
               AVG(e.salary) AS avgSalary,
               MAX(e.salary) AS maxSalary,
               MIN(e.salary) AS minSalary
        FROM Employee e
        JOIN e.department d
        GROUP BY d.name
        ORDER BY total DESC
        """)
    List<DeptStatProjection> getDepartmentStatistics();

    /**
     * Lương trung bình toàn hệ thống
     */
    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.salary IS NOT NULL")
    Double getAverageSalary();

    /**
     * Tổng quỹ lương
     */
    @Query("SELECT SUM(e.salary) FROM Employee e WHERE e.salary IS NOT NULL")
    Double getTotalSalary();

    /**
     * Lương cao nhất
     */
    @Query("SELECT MAX(e.salary) FROM Employee e WHERE e.salary IS NOT NULL")
    Double getMaxSalary();

    /**
     * Lương thấp nhất
     */
    @Query("SELECT MIN(e.salary) FROM Employee e WHERE e.salary IS NOT NULL")
    Double getMinSalary();
}
