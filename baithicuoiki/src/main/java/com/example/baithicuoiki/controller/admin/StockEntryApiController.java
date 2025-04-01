package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.dto.StockEntryDTO;
import com.example.baithicuoiki.model.Product;
import com.example.baithicuoiki.model.StockEntry;
import com.example.baithicuoiki.model.Supplier;
import com.example.baithicuoiki.service.ProductService;
import com.example.baithicuoiki.service.StockEntryService;
import com.example.baithicuoiki.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class StockEntryApiController {
    @Autowired
    private ProductService productService;
    @Autowired
    private StockEntryService stockEntryService;
    @Autowired
    private SupplierService supplierService;

    @GetMapping("/stock_entry")
    public ResponseEntity<?> getAllStockEntries() {
        List<StockEntry> stockEntries = stockEntryService.getAllStockEntries();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy danh sách nhập kho thành công");
        response.put("stock_entries", stockEntries);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stock_entry")
    public ResponseEntity<Map<String, Object>> addStockEntry(@RequestBody StockEntryDTO stockEntryDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = productService.getProductById(stockEntryDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + stockEntryDTO.getProductId()));

            Supplier supplier = supplierService.getSupplierById(stockEntryDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + stockEntryDTO.getSupplierId()));

            StockEntry stockEntry = new StockEntry();
            stockEntry.setProduct(product);
            stockEntry.setSupplier(supplier);
            stockEntry.setQuantity(stockEntryDTO.getQuantity());
            stockEntry.setPrice(stockEntryDTO.getPrice());

            StockEntry savedStockEntry = stockEntryService.addStockEntry(stockEntry);
            response.put("message", "Tạo bản ghi nhập kho thành công");
            response.put("stock_entry", savedStockEntry);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/stock_entry/{id}")
    public ResponseEntity<Map<String, Object>> updateStockEntry(@PathVariable Long id, @RequestBody StockEntryDTO stockEntryDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            StockEntry existingStockEntry = stockEntryService.getStockEntryById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi nhập kho với ID: " + id));

            Product product = productService.getProductById(stockEntryDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + stockEntryDTO.getProductId()));

            Supplier supplier = supplierService.getSupplierById(stockEntryDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + stockEntryDTO.getSupplierId()));

            existingStockEntry.setProduct(product);
            existingStockEntry.setSupplier(supplier);
            existingStockEntry.setQuantity(stockEntryDTO.getQuantity());
            existingStockEntry.setPrice(stockEntryDTO.getPrice());

            StockEntry updatedStockEntry = stockEntryService.updateStockEntry(existingStockEntry);
            response.put("message", "Cập nhật bản ghi nhập kho thành công");
            response.put("stock_entry", updatedStockEntry);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/stock_entry/{id}")
    public ResponseEntity<Map<String, Object>> deleteStockEntry(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            stockEntryService.deleteStockEntryById(id);
            response.put("message", "Xóa bản ghi nhập kho thành công");
            response.put("isSuccess", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            response.put("isSuccess", false);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
