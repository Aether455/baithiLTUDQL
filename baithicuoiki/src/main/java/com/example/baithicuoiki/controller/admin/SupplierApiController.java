package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.model.Supplier;
import com.example.baithicuoiki.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class SupplierApiController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping("/supplier")
    public ResponseEntity<?> getAllSuppliers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Supplier> suppliers = supplierService.getAllSupplier();
            response.put("message", "Lấy danh sách nhà cung cấp thành công");
            response.put("suppliers", suppliers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Lỗi khi lấy danh sách nhà cung cấp");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/supplier/{id}")
    public ResponseEntity<Map<String, Object>> getSupplierById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Supplier supplier = supplierService.getSupplierById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + id));

            response.put("message", "Lấy thông tin nhà cung cấp thành công");
            response.put("supplier", supplier);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/supplier")
    public ResponseEntity<Map<String, Object>> addSupplier(@RequestBody Supplier supplier) {
        Map<String, Object> response = new HashMap<>();
        try {
            String phone = supplier.getPhone();
            String email = supplier.getEmail();

            List<String> errors = new ArrayList<>();
            if (supplierService.existsByPhone(phone)) errors.add("Số điện thoại đã tồn tại!");
            if (supplierService.existsByEmail(email)) errors.add("Email đã tồn tại!");

            if (!errors.isEmpty()) {
                response.put("message", String.join(" ", errors));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Supplier newSupplier = supplierService.addSupplier(supplier);
            response.put("message", "Thêm nhà cung cấp thành công");
            response.put("supplier", newSupplier);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Lỗi khi thêm nhà cung cấp");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/supplier/{id}")
    public ResponseEntity<Map<String, Object>> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplierDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            Supplier existingSupplier = supplierService.getSupplierById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + id));

            String newPhone = supplierDetails.getPhone();
            String newEmail = supplierDetails.getEmail();

            List<String> errors = new ArrayList<>();
            if (!existingSupplier.getPhone().equals(newPhone) && supplierService.existsByPhone(newPhone)) {
                errors.add("Số điện thoại đã tồn tại!");
            }
            if (!existingSupplier.getEmail().equals(newEmail) && supplierService.existsByEmail(newEmail)) {
                errors.add("Email đã tồn tại!");
            }

            if (!errors.isEmpty()) {
                response.put("message", String.join(" ", errors));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            existingSupplier.setName(supplierDetails.getName());
            existingSupplier.setEmail(newEmail);
            existingSupplier.setPhone(newPhone);
            existingSupplier.setAddress(supplierDetails.getAddress());

            Supplier updatedSupplier = supplierService.updateSupplier(existingSupplier);
            response.put("message", "Cập nhật nhà cung cấp thành công");
            response.put("supplier", updatedSupplier);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/supplier/{id}")
    public ResponseEntity<Map<String, Object>> deleteSupplier(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            supplierService.deleteSupplierById(id);
            response.put("message", "Xóa nhà cung cấp thành công");
            response.put("isSuccess", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Xóa nhà cung cấp thất bại");
            response.put("isSuccess", false);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
