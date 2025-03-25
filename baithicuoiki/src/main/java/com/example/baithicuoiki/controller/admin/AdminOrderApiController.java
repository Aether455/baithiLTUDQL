package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.dto.OrderRequestDTO;
import com.example.baithicuoiki.model.Order;
import com.example.baithicuoiki.model.OrderDetail;
import com.example.baithicuoiki.model.User;
import com.example.baithicuoiki.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Orders retrieved successfully");
            response.put("orders", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
//            return ResponseEntity.ok(order);
            return ResponseEntity.ok(Map.of("message", "Order retrieved successfully", "order", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Order not found"));
        }
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable Long orderId) {
        try {
            List<OrderDetail> orderDetails = orderService.getOrderDetails(orderId);
            return ResponseEntity.ok(Map.of("message", "Order details retrieved successfully", "orderDetails", orderDetails));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Order details not found"));
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, String>> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderRequestDTO orderRequest) {
        try {
            orderService.updateOrderStatus(orderId, orderRequest.getStatus());
            return ResponseEntity.ok(Map.of("message", "Order status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok(Map.of("message", "Order deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Order not found"));
        }
    }

    // Đặt hàng mới
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody OrderRequestDTO orderRequest,
            @AuthenticationPrincipal User user) {
        try {
            // Kiểm tra số lượng sản phẩm trong kho
            List<String> outOfStockProducts = orderService.checkStockAndGetUnavailableProducts(orderRequest.getCartItems());
            if (!outOfStockProducts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Sản phẩm " + String.join(", ", outOfStockProducts)+ " không đủ hàng!"));
            }
            Order order = orderService.createOrder(
                    orderRequest.getCustomerName(),
                    orderRequest.getShippingAddress(),
                    orderRequest.getPhoneNumber(),
                    orderRequest.getNotes(),
                    orderRequest.getPaymentMethod(),
                    orderRequest.getCartItems(),
                    user
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Đặt hàng thành công ", "order" , order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Đặt hàng thất bại!"));
        }
    }
}
