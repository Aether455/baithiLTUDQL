package com.example.baithicuoiki.security;

import com.example.baithicuoiki.security.jwt.JwtAuthenticationFilter;
import com.example.baithicuoiki.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration // Đánh dấu đây là một class cấu hình của Spring.
@RequiredArgsConstructor // Lombok tự động tạo constructor chứa các final fields.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Bộ lọc JWT để xác thực request.
    private final CustomUserDetailsService customUserDetailsService; // Service để lấy thông tin người dùng.

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Cho phép gửi cookie và authentication headers.
        config.setAllowedOrigins(List.of("http://localhost:5173")); // Cho phép React frontend truy cập.
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type")); // Các header được chấp nhận.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Các phương thức HTTP được chấp nhận.
        source.registerCorsConfiguration("/**", config); // Áp dụng CORS cho tất cả các endpoints.
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true);
                    config.setAllowedOrigins(List.of("http://localhost:5173")); // Cho phép frontend React truy cập API.
                    config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    return config;
                })) // Cấu hình CORS
                .csrf(AbstractHttpConfigurer::disable) // Vô hiệu hóa CSRF (vì API hoạt động theo mô hình stateless).
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Cho phép truy cập không cần xác thực vào các endpoint liên quan đến xác thực.
                        .requestMatchers("/api/admin/**", "/api/statistics/**").hasRole("ADMIN") // Chỉ ADMIN mới có quyền truy cập các endpoint này.
                        .requestMatchers("/api/user/**", "/api/cart/**").hasAnyRole("USER", "ADMIN") // USER & ADMIN có quyền truy cập.
                        .anyRequest().authenticated()  // Các API khác yêu cầu xác thực.
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sử dụng stateless session, không lưu session trên server.
                .authenticationProvider(authenticationProvider()) // Cấu hình provider cho xác thực người dùng.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Thêm bộ lọc JWT trước bộ lọc xác thực mặc định.

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService); // Xác thực người dùng từ database.
        authProvider.setPasswordEncoder(passwordEncoder()); // Mã hóa mật khẩu bằng BCrypt.
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // Quản lý xác thực.
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Mã hóa mật khẩu với thuật toán BCrypt.
    }
}
