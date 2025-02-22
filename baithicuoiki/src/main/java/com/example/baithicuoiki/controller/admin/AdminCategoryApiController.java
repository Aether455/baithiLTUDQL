package com.example.baithicuoiki.controller.admin;


import com.example.baithicuoiki.model.Category;
import com.example.baithicuoiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryApiController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public void createCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails){
        Category category =categoryService.getCategoryById(id).orElseThrow(() -> new RuntimeException("Product not found on :: " + id));
        category.setName(categoryDetails.getName());
        final Category updateCategory = categoryService.updateCategory(category);

        return ResponseEntity.ok(updateCategory);
    }
    @DeleteMapping("/{id}")
    public  ResponseEntity<?> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
