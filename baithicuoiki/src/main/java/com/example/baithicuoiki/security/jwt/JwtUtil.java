package com.example.baithicuoiki.security.jwt;

import com.example.baithicuoiki.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service // Đánh dấu class này là một Spring service component, giúp Spring quản lý nó như một Bean.
public class JwtUtil {

    @Value("${jwt.secret}") // Inject giá trị `jwt.secret` từ file cấu hình vào biến `SECRET_KEY`.
    private String SECRET_KEY;

    // Trích xuất username từ token JWT.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Lấy giá trị `subject` từ token (chính là username).
    }

    // Hàm generic để trích xuất một claim cụ thể từ token.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Lấy toàn bộ claims từ token.
        return claimsResolver.apply(claims); // Áp dụng function để lấy claim mong muốn.
    }

    // Tạo JWT từ thông tin của UserDetails.
    public String generateToken(UserDetails userDetails) {
        User user = (User) userDetails; // Ép kiểu từ `UserDetails` sang `User`.
        Map<String, Object> claims = new HashMap<>(); // Tạo một map chứa claims bổ sung.

        // Thêm danh sách roles của user vào claims.
        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Lấy tên của mỗi quyền (role).
                .collect(Collectors.toList())); // Chuyển thành danh sách.

        return Jwts.builder()
                .setClaims(claims) // Gán claims vào token.
                .setSubject(user.getUsername()) // Đặt username làm subject của token.
                .setIssuedAt(new Date(System.currentTimeMillis())) // Thời gian phát hành token.
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Thời gian hết hạn (10 giờ).
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Ký token bằng thuật toán HS256.
                .compact(); // Tạo chuỗi JWT hoàn chỉnh.
    }

    // Kiểm tra tính hợp lệ của token với userDetails.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Lấy username từ token.
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Kiểm tra username và hạn token.
    }

    // Kiểm tra xem token có hết hạn hay không.
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date()); // So sánh thời gian hết hạn với hiện tại.
    }

    // Lấy tất cả claims từ token.
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // Tạo parser JWT.
                .verifyWith(getSigningKey()) // Xác minh token với khóa bí mật.
                .build()
                .parseSignedClaims(token) // Phân tích token để lấy claims.
                .getPayload();
    }

    // Lấy khóa bí mật dùng để ký JWT.
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Giải mã khóa từ chuỗi base64.
        return Keys.hmacShaKeyFor(keyBytes); // Tạo SecretKey sử dụng thuật toán HMAC.
    }

    // Trích xuất danh sách roles từ token JWT.
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class)); // Lấy danh sách roles từ claims.
    }
}

