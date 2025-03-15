package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.model.Supplier;
import com.example.baithicuoiki.repository.SupplierRepository;
import com.example.baithicuoiki.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/supplier")
public class SupplierApiController {
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public ResponseEntity<?> getAllSupplier() {
        List<Supplier> suppliers = supplierService.getAllSupplier();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Suppliers retrieved successfully");
        response.put("suppliers", suppliers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSupplierById(@PathVariable Long id) {
        Optional<Supplier> supplier = supplierService.getSupplierById(id);
        if (supplier.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Supplier retrieved successfully");
            response.put("supplier", supplier.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Supplier not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addSupplier(@RequestBody Supplier supplier) {
        Supplier newSupplier = supplierService.addSupplier(supplier);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Supplier added successfully");
        response.put("supplier", newSupplier);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplierDetails) {
        Supplier supplier = supplierService.getSupplierById(id).orElse(null);
        if (supplier == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Supplier not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        supplier.setName(supplierDetails.getName());
        supplier.setEmail(supplierDetails.getEmail());
        supplier.setPhone(supplierDetails.getPhone());
        supplier.setAddress(supplierDetails.getAddress());

        final Supplier updateSupplier = supplierService.updateSupplier(supplier);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Supplier updated successfully");
        response.put("supplier", updateSupplier);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSupplier(@PathVariable Long id) {
        boolean deleted = supplierService.deleteSupplierById(id);
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Supplier deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Supplier not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
