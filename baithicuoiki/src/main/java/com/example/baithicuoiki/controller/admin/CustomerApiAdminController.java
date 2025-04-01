package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.model.Customer;
import com.example.baithicuoiki.repository.CustomerRepository;
import com.example.baithicuoiki.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/customer")
@RequiredArgsConstructor
public class CustomerApiAdminController {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy danh sách khách hàng thành công");
        response.put("customers", customers);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, ?>> addCustomer(@RequestBody Customer customer) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (customerService.existsByPhone(customer.getPhone())){
                response.put("message", "Số điện thoại đã tồn tại!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            Customer savedCustomer = customerService.saveCustomer(customer);
            response.put("message", "Tạo khách hàng thành công");
            response.put("customer", savedCustomer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            response.put("message", "Dữ liệu khách hàng không hợp lệ");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, ?>> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
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
            // Cập nhật thông tin khách hàng
            customer.setId(id);
            Customer updatedCustomer = customerService.updateCustomer(customer);
            response.put("message", "Cập nhật khách hàng thành công");
            response.put("customer", updatedCustomer);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            response.put("message", "Không tìm thấy khách hàng");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCustomerById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            customerService.deleteCustomerById(id);
            response.put("message", "Xóa khách hàng thành công");
            response.put("isSuccess", true);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            response.put("isSuccess", false);
            response.put("message", "Không tìm thấy khách hàng");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
