package com.example.baithicuoiki.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component // Đánh dấu class này là một Spring Bean để Spring quản lý.
@RequiredArgsConstructor // Lombok tự động tạo constructor chứa các final fields.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // Đối tượng xử lý JWT.
    private final UserDetailsService userDetailsService; // Service để lấy thông tin UserDetails từ username.

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // Lấy giá trị của header "Authorization".
        final String jwt;
        final String username;

        // Bỏ qua việc kiểm tra JWT cho các endpoint `/api/auth/` (đăng ký, đăng nhập).
        if (request.getServletPath().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Kiểm tra header Authorization có chứa Bearer token không.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Cắt bỏ chuỗi "Bearer " để lấy token.
        username = jwtUtil.extractUsername(jwt); // Trích xuất username từ token.

        try {
            // Kiểm tra nếu username hợp lệ và chưa có authentication trong SecurityContext.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Lấy thông tin UserDetails.

                // Kiểm tra tính hợp lệ của token.
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    // Trích xuất roles từ token và chuyển thành danh sách GrantedAuthority.
                    List<GrantedAuthority> authorities = jwtUtil.extractRoles(jwt).stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // Tạo UsernamePasswordAuthenticationToken để xác thực người dùng.
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    // Đặt authentication vào SecurityContext.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired or invalid");
                    return;
                }
            }
        } catch (ExpiredJwtException e) { // Bắt lỗi khi token đã hết hạn.
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
            return;
        }

        // Tiếp tục chuyển request đến filter tiếp theo trong chain.
        filterChain.doFilter(request, response);
    }
}
