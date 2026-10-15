package com.example.productcatalog.service;

import com.example.productcatalog.entity.Product;
import com.example.productcatalog.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Get paginated products
    public Page<Product> getProductsPaginated(int page, int size, String search, String category, String sortBy) {
        Sort sort = Sort.by("id").descending();
        if (sortBy != null && !sortBy.isEmpty()) {
            sort = Sort.by(sortBy).ascending();
        }
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        if (search != null && !search.isEmpty()) {
            return productRepository.searchProducts(search, pageRequest);
        }
        if (category != null && !category.isEmpty()) {
            return productRepository.findByCategory(category, pageRequest);
        }
        
        return productRepository.findAll(pageRequest);
    }
    
    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Get product by ID
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }
    
    // Save product
    public Product saveProduct(Product product) {
        log.info("Saving product: {}", product.getName());
        return productRepository.save(product);
    }
    
    // Delete product
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
    }
    
    // Get all categories
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
    
    // Get statistics
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalProducts = productRepository.count();
        List<Product> lowStock = productRepository.findLowStockProducts(10);
        BigDecimal totalValue = productRepository.getTotalInventoryValue();
        
        stats.put("totalProducts", totalProducts);
        stats.put("lowStockProducts", lowStock.size());
        stats.put("totalInventoryValue", totalValue);
        stats.put("categories", productRepository.countByCategory());
        
        return stats;
    }
    
    // Get products by price range
    public List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }
    
    // Bulk delete products
    public void bulkDeleteProducts(List<Long> ids) {
        productRepository.deleteAllById(ids);
    }
    
    // Update stock quantity
    public Product updateStock(Long id, int quantity) {
        Product product = getProduct(id);
        product.setQuantity(quantity);
        return productRepository.save(product);
    }
}
