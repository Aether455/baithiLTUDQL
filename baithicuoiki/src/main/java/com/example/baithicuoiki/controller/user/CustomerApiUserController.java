package com.example.baithicuoiki.controller.user;

import com.example.baithicuoiki.model.Customer;
import com.example.baithicuoiki.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/customer")
@RequiredArgsConstructor
public class CustomerApiUserController {

    private final CustomerService customerService;

    // Lấy thông tin khách hàng của chính người dùng (Chỉ cho phép User truy cập thông tin của mình)
    @GetMapping("/{userId}")
    public ResponseEntity<Customer> getCustomerByUserId(@PathVariable Long userId) {
        Optional<Customer> customer = customerService.getCustomerByUserId(userId);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Cập nhật thông tin khách hàng của chính người dùng (Chỉ cho phép User cập nhật thông tin của mình)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Lấy thông tin khách hàng hiện tại từ database
            Customer existingCustomer = customerService.getCustomerById(id);
            if (existingCustomer == null) {
                response.put("message", "Không tìm thấy khách hàng!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Kiểm tra nếu số điện thoại thay đổi và đã tồn tại trên hệ thống
            if (!existingCustomer.getPhone().equals(customer.getPhone()) && customerService.existsByPhone(customer.getPhone())) {
                response.put("message", "Số điện thoại đã tồn tại!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            customer.setId(id);
            Customer updatedCustomer = customerService.updateCustomer(customer);
            response.put("message", "Customer updated successfully");
            response.put("customer", updatedCustomer);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            response.put("message", "Customer not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}
