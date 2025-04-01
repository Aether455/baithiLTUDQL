package com.example.baithicuoiki.service;

import com.example.baithicuoiki.model.Supplier;
import com.example.baithicuoiki.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public List<Supplier> getAllSupplier(){
        return supplierRepository.findAll();
    }
    public Optional<Supplier> getSupplierById(Long id){
        return supplierRepository.findById(id);
    }

    public boolean existsByPhone(String phone){
        return supplierRepository.existsByPhone(phone);
    }
    public boolean existsByEmail(String email){
        return supplierRepository.existsByEmail(email);
    }

    public Supplier addSupplier(Supplier supplier){

        if (supplierRepository.existsByPhone(supplier.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại đã tồn tại!");
        }
        if (supplierRepository.existsByEmail(supplier.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại!");
        }

        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(@NonNull Supplier supplier){
        Supplier existingSupplier= supplierRepository.findById(supplier.getId()).orElseThrow(()-> new IllegalStateException("Supplier with ID " + supplier.getId() + " does not exist."));
        // Kiểm tra số điện thoại và email có bị trùng không (trừ chính nó)
        if (!existingSupplier.getPhone().equals(supplier.getPhone()) && supplierRepository.existsByPhone(supplier.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại đã tồn tại!");
        }
        if (!existingSupplier.getEmail().equals(supplier.getEmail()) && supplierRepository.existsByEmail(supplier.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại!");
        }
        existingSupplier.setName(supplier.getName());
        existingSupplier.setEmail(supplier.getEmail());
        existingSupplier.setPhone(supplier.getPhone());
        existingSupplier.setAddress(supplier.getAddress());

        return supplierRepository.save(existingSupplier);
    }

    // Xóa nhà cung cấp
    public boolean deleteSupplierById(Long id) {
        if (supplierRepository.existsById(id)) {
            supplierRepository.deleteById(id);
            return true;
        }
        return false;
    }


}
