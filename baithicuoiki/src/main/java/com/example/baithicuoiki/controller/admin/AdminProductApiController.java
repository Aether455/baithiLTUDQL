package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.model.Category;
import com.example.baithicuoiki.model.Product;
import com.example.baithicuoiki.service.CategoryService;
import com.example.baithicuoiki.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/admin/product")
public class AdminProductApiController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        try {
            Category category = categoryService.getCategoryById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
            Product savedProduct = productService.addProduct(product);
            response.put("message", "Product created successfully");
            response.put("product", savedProduct);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found on :: " + id));
            response.put("message", "Product retrieved successfully");
            response.put("product", product);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @RequestBody Product productDetail) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found on :: " + id));
            Category category = categoryService.getCategoryById(productDetail.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setName(productDetail.getName());
            product.setPrice(productDetail.getPrice());
            product.setDescription(productDetail.getDescription());
            product.setImage(productDetail.getImage());
            product.setCategory(category);
            product.setStatus(productDetail.getStatus());
            product.setSize(productDetail.getSize());
            product.setWoodType(productDetail.getWoodType());
            product.setQuantity(productDetail.getQuantity());

            Product updatedProduct = productService.addProduct(product);
            response.put("message", "Product updated successfully");
            response.put("product", updatedProduct);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found on :: " + id));
            productService.deleteProductById(id);
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
