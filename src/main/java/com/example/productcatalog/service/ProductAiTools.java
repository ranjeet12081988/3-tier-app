package com.example.productcatalog.service;

import com.example.productcatalog.entity.Product;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProductAiTools {

    private final ProductService productService;

    public ProductAiTools(ProductService productService) {
        this.productService = productService;
    }

    @Tool(description = "Search for a specific product in the catalog using its exact SKU code")
    public String getProductBySku(String sku) {
        try {
            Optional<Product> productOpt = productService.findBySku(sku);

            if (productOpt.isEmpty()) {
                return "No product found with SKU: " + sku;
            }

            Product product = productOpt.get();
            return String.format("Product Found: %s | Price: $%s | Stock Quantity: %d",
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity());
        } catch (Exception e) {
            return "Error searching for product: " + e.getMessage();
        }
    }

    @Tool(description = "Get the total number of products currently in the catalog")
    public String getTotalProductCount() {
        return "There are currently " + productService.getAllProducts().size() + " products in the catalog.";
    }

    @Tool(description = "Create a new product in the catalog. Provide name, sku, price, and quantity.")
    public String createProduct(String name, String sku, double price, int quantity) {
        try {
            Product p = new Product();
            p.setName(name);
            p.setSku(sku);
            p.setPrice(BigDecimal.valueOf(price));
            p.setQuantity(quantity);

            productService.saveProduct(p);

            return "Successfully created product: " + name + " with SKU: " + sku;
        } catch (Exception e) {
            return "Failed to create product: " + e.getMessage();
        }
    }
}