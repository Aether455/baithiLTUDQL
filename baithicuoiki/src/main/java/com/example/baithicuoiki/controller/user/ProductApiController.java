package com.example.baithicuoiki.controller.user;

import com.example.baithicuoiki.model.Product;
import com.example.baithicuoiki.service.CategoryService;
import com.example.baithicuoiki.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/product")
public class ProductApiController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;


    @GetMapping
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }



}
