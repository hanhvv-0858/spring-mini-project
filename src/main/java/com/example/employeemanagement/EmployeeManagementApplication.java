package com.example.employeemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *  @EnableScheduling → Kích hoạt @Scheduled trên toàn app
 *  @EnableCaching    → Kích hoạt @Cacheable, @CacheEvict trên toàn app
 */
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class EmployeeManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementApplication.class, args);
	}
}
