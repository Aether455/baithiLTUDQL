package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.dto.OrderRequestDTO;
import com.example.baithicuoiki.model.Order;
import com.example.baithicuoiki.model.OrderDetail;
import com.example.baithicuoiki.model.User;
import com.example.baithicuoiki.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderApiController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("orders", orderService.getAllOrders());
            response.put("message", "Lấy danh sách đơn hàng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("order", orderService.getOrderById(orderId));
            response.put("message", "Lấy thông tin đơn hàng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Không tìm thấy đơn hàng");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("orderDetails", orderService.getOrderDetails(orderId));
            response.put("message", "Lấy chi tiết đơn hàng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Không tìm thấy chi tiết đơn hàng");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderRequestDTO orderRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.updateOrderStatus(orderId, orderRequest.getStatus());
            response.put("message", "Cập nhật trạng thái đơn hàng thành công");
            response.put("isSuccess", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Cập nhật trạng thái đơn hàng thất bại");
            response.put("isSuccess", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.deleteOrder(orderId);
            response.put("message", "Xóa đơn hàng thành công");
            response.put("isSuccess", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Không tìm thấy đơn hàng");
            response.put("isSuccess", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody OrderRequestDTO orderRequest,
            @AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<String> outOfStockProducts = orderService.checkStockAndGetUnavailableProducts(orderRequest.getCartItems());
            if (!outOfStockProducts.isEmpty()) {
                response.put("message", "Sản phẩm " + String.join(", ", outOfStockProducts) + " không đủ hàng!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            response.put("order", orderService.createOrder(
                    orderRequest.getCustomerName(),
                    orderRequest.getShippingAddress(),
                    orderRequest.getPhoneNumber(),
                    orderRequest.getNotes(),
                    orderRequest.getPaymentMethod(),
                    orderRequest.getCartItems(),
                    user));
            response.put("message", "Tạo đơn hàng thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "Tạo đơn hàng thất bại!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}