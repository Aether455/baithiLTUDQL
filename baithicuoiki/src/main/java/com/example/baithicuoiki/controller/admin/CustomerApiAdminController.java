package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.model.Customer;
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

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customers retrieved successfully");
        response.put("customers", customers);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, ?>> addCustomer(@RequestBody Customer customer) {
        try {
            Customer savedCustomer = customerService.saveCustomer(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Customer created successfully");
            response.put("customer", savedCustomer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid customer data");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, ?>> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            customer.setId(id);
            Customer updatedCustomer = customerService.updateCustomer(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Customer updated successfully");
            response.put("customer", updatedCustomer);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Customer not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCustomerById(@PathVariable Long id) {
        try {
            customerService.deleteCustomerById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Customer deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Customer not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
