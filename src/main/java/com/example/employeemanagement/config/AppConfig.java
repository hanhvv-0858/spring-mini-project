package com.example.employeemanagement.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration  // Nói với Spring: class này chứa định nghĩa Bean
public class AppConfig {
    /**
     * PasswordEncoder Bean
     *
     * Tại sao dùng @Bean thay vì @Component?
     * BCryptPasswordEncoder là class của Spring Security (thư viện ngoài)
     * → Ta không thể thêm @Component vào class đó được
     * → Dùng @Bean để Spring quản lý instance do ta tạo
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // strength=12: số vòng hash (default=10, cao hơn=an toàn hơn nhưng chậm hơn)
        return new BCryptPasswordEncoder(12);
    }
    /**
     * ModelMapper Bean — dùng để convert Entity ↔ DTO (Module 3+)
     *
     * Cấu hình STRICT để tránh mapping nhầm field
     */

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);  // Không ghi đè field nếu source là null
        return mapper;
    }


}
