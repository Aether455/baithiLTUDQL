package com.example.baithicuoiki.service;

import com.example.baithicuoiki.model.Product;
import com.example.baithicuoiki.model.StockEntry;
import com.example.baithicuoiki.model.Supplier;
import com.example.baithicuoiki.repository.ProductRepository;
import com.example.baithicuoiki.repository.StockEntryRepository;
import com.example.baithicuoiki.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockEntryService {
    private final StockEntryRepository stockEntryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public List<StockEntry> getAllStockEntries() {
        return stockEntryRepository.findAll();
    }

    public Optional<StockEntry> getStockEntryById(Long id) {
        return stockEntryRepository.findById(id);
    }

    public StockEntry addStockEntry(StockEntry stockEntry) {
        Product product = productRepository.findById(stockEntry.getProduct().getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " + stockEntry.getProduct().getId() + " does not exist."));

        Supplier supplier = supplierRepository.findById(stockEntry.getSupplier().getId())
                .orElseThrow(() -> new IllegalStateException("Supplier with ID " + stockEntry.getSupplier().getId() + " does not exist."));

        product.setQuantity(product.getQuantity() + stockEntry.getQuantity());

        productRepository.save(product);
        return stockEntryRepository.save(stockEntry);
    }
    @Transactional
    public StockEntry updateStockEntry(@NonNull StockEntry stockEntry) {
        StockEntry existingStockEntry = stockEntryRepository.findById(stockEntry.getId())
                .orElseThrow(() -> new IllegalStateException("Stock entry with ID " + stockEntry.getId() + " does not exist."));

        Product product = existingStockEntry.getProduct(); //  Lấy sản phẩm từ nhập kho cũ
        Supplier supplier = supplierRepository.findById(stockEntry.getSupplier().getId())
                .orElseThrow(() -> new IllegalStateException("Supplier with ID " + stockEntry.getSupplier().getId() + " does not exist."));

        int oldQuantity = existingStockEntry.getQuantity();
        int newQuantity = stockEntry.getQuantity();

        // Cập nhật số lượng sản phẩm: trừ số lượng cũ, cộng số lượng mới
        int updatedQuantity = product.getQuantity() - oldQuantity + newQuantity;

        if (updatedQuantity < 0) {
            throw new IllegalStateException("Không thể cập nhật, số lượng trong kho không đủ.");
        }

        product.setQuantity(updatedQuantity);
        productRepository.save(product); //  Cập nhật số lượng trước khi lưu nhập kho

        existingStockEntry.setSupplier(supplier);
        existingStockEntry.setQuantity(newQuantity);
        existingStockEntry.setPrice(stockEntry.getPrice());

        return stockEntryRepository.save(existingStockEntry);
    }


    public void deleteStockEntryById(Long id) {
        StockEntry stockEntry = stockEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Stock entry with ID " + id + " does not exist."));

        Product product = stockEntry.getProduct();

        if (product.getQuantity() >= stockEntry.getQuantity()) {
            product.setQuantity(product.getQuantity() - stockEntry.getQuantity());
        } else {
            throw new IllegalStateException("Không thể xóa vì số lượng trong kho không đủ.");
        }

        productRepository.save(product);
        stockEntryRepository.deleteById(id);
    }
}
