package com.example.baithicuoiki.controller.user;

import com.example.baithicuoiki.dto.CartRequestDTO;
import com.example.baithicuoiki.dto.RemoveSelectedDTO;
import com.example.baithicuoiki.model.User;
import com.example.baithicuoiki.security.jwt.JwtUtil;
import com.example.baithicuoiki.service.CartService;
import com.example.baithicuoiki.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {
    @Autowired
    private final CartService cartService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtUtil jwtUtil;

    private User getAuthenticatedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Bạn chưa đăng nhập.");
        }

        String jwt = authHeader.substring(7);
        return userService.findByUsername(jwtUtil.extractUsername(jwt))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = getAuthenticatedUser(request);
            response.put("message", "Lấy giỏ hàng thành công");
            response.put("cartItems", cartService.getCartItems(user));
            response.put("totalCartPrice", cartService.getTotalCartPrice(user));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody CartRequestDTO cartRequest, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = getAuthenticatedUser(request);
            cartService.addToCart(user, cartRequest.getProductId(), cartRequest.getQuantity());
            response.put("message", "Sản phẩm đã được thêm vào giỏ hàng.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/removeSelected")
    public ResponseEntity<Map<String, Object>> removeSelected(@RequestBody RemoveSelectedDTO removeRequest, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = getAuthenticatedUser(request);
            if (removeRequest.getProductIds() != null && !removeRequest.getProductIds().isEmpty()) {
                removeRequest.getProductIds().forEach(productId -> cartService.removeFromCart(user, productId));
                response.put("message", "Đã xóa các sản phẩm khỏi giỏ hàng.");
                return ResponseEntity.ok(response);
            } else {
                throw new RuntimeException("Danh sách sản phẩm cần xóa không hợp lệ.");
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(@Valid @PathVariable Long productId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = getAuthenticatedUser(request);
            cartService.removeFromCart(user, productId);
            response.put("message", "Sản phẩm đã được xóa khỏi giỏ hàng.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateCartItem(@RequestBody CartRequestDTO cartRequest, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = getAuthenticatedUser(request);
            if (cartRequest.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng sản phẩm phải lớn hơn 0.");
            }
            cartService.updateCartItemQuantity(user, cartRequest.getProductId(), cartRequest.getQuantity());
            response.put("message", "Số lượng sản phẩm đã được cập nhật.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = getAuthenticatedUser(request);
            cartService.clearCard(user);
            response.put("message", "Giỏ hàng đã được làm trống.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
