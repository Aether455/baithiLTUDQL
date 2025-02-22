package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.model.Supplier;
import com.example.baithicuoiki.repository.SupplierRepository;
import com.example.baithicuoiki.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/supplier")
public class SupplierApiController {
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public List<Supplier> getAllSupplier(){
        return supplierService.getAllSupplier();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Optional<Supplier> supplier = supplierService.getSupplierById(id);
        return supplier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Supplier> addSupplier(@RequestBody Supplier supplier) {
        Supplier newSupplier = supplierService.addSupplier(supplier);
        return ResponseEntity.ok(newSupplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplierDetails) {
        Supplier supplier = supplierService.getSupplierById(id).orElseThrow(() -> new RuntimeException("Supplier not found on :: " + id));
        supplier.setName(supplierDetails.getName());
        supplier.setEmail(supplierDetails.getEmail());
        supplier.setPhone(supplierDetails.getPhone());
        supplier.setAddress(supplierDetails.getAddress());

        final Supplier updateSupplier= supplierService.updateSupplier(supplier);

        return ResponseEntity.ok(updateSupplier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        boolean deleted = supplierService.deleteSupplierById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
