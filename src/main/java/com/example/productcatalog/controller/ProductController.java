package com.example.productcatalog.controller;

import com.example.productcatalog.entity.Product;
import com.example.productcatalog.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Web - List with pagination
    @GetMapping
    public String listProducts(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy) {

        Page<Product> productPage = productService.getProductsPaginated(page, size, search, category, sortBy);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("statistics", productService.getStatistics());

        return "products";
    }

    // REST API - Get all products with pagination
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductsAPI(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Page<Product> productPage = productService.getProductsPaginated(page, size, search, null, null);

        Map<String, Object> response = Map.of(
                "products", productPage.getContent(),
                "currentPage", page,
                "totalPages", productPage.getTotalPages(),
                "totalItems", productPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    // REST API - Get product by ID
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // REST API - Create product
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Product> createProductAPI(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.saveProduct(product));
    }

    // REST API - Update product
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Product> updateProductAPI(@PathVariable Long id,
            @RequestBody Product product) {
        product.setId(id);
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    // REST API - Delete product
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProductAPI(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Web - Add form
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.getAllCategories());
        return "add-product";
    }

    // Web - Add product
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
            RedirectAttributes redirectAttributes) {
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    // Web - Edit form
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProduct(id));
        model.addAttribute("categories", productService.getAllCategories());
        return "edit-product";
    }

    // Web - Update product
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
            @ModelAttribute Product product,
            RedirectAttributes redirectAttributes) {
        product.setId(id);
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
        return "redirect:/products";
    }

    // Web - Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        return "redirect:/products";
    }

    // Statistics endpoint
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(productService.getStatistics());
    }
}
