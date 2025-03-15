package com.example.baithicuoiki.controller.admin;

import com.example.baithicuoiki.model.Category;
import com.example.baithicuoiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryApiController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Category category) {
        Map<String, Object> response = new HashMap<>();
        try {
            categoryService.addCategory(category);
            response.put("message", "Category created successfully");
            response.put("category", category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to create category: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            Category category = categoryService.getCategoryById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found on :: " + id));
            category.setName(categoryDetails.getName());
            Category updatedCategory = categoryService.updateCategory(category);

            response.put("message", "Category updated successfully");
            response.put("category", updatedCategory);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to update category: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            categoryService.deleteCategory(id);
            response.put("message", "Xóa danh mục thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Xoá danh mục thất bại!");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
