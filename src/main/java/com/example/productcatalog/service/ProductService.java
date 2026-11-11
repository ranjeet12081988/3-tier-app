package com.example.productcatalog.service;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.example.productcatalog.entity.Product;
import com.example.productcatalog.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional; // <-- ADDED

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

    // ====================================================================
    // NEW METHOD ADDED FOR AI AGENT
    // ====================================================================
    public Optional<Product> findBySku(String sku) {
        return productRepository.findAll().stream()
                .filter(p -> p.getSku() != null && p.getSku().equalsIgnoreCase(sku))
                .findFirst();
    }
    // ====================================================================

    @Transactional
    public int saveProductsFromCsv(InputStream inputStream) throws Exception {
        List<Product> products = new ArrayList<>();

        try (BOMInputStream bomIn = new BOMInputStream(inputStream);
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(bomIn, StandardCharsets.UTF_8));
                CSVParser csvParser = new CSVParser(fileReader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()
                                .withIgnoreEmptyLines())) {

            for (CSVRecord csvRecord : csvParser) {
                Product product = new Product();
                try {
                    // Check for 'name' or 'product_name'
                    if (csvRecord.isMapped("name")) {
                        product.setName(csvRecord.get("name"));
                    } else if (csvRecord.isMapped("product_name")) {
                        product.setName(csvRecord.get("product_name"));
                    } else {
                        throw new IllegalArgumentException("Missing name or product_name column");
                    }

                    // Check for 'sku' or 'product_id'
                    if (csvRecord.isMapped("sku")) {
                        product.setSku(csvRecord.get("sku"));
                    } else if (csvRecord.isMapped("product_id")) {
                        product.setSku(csvRecord.get("product_id"));
                    } else {
                        throw new IllegalArgumentException("Missing sku or product_id column");
                    }

                    // Safely handle price (default to 0 if not found)
                    if (csvRecord.isMapped("price") && !csvRecord.get("price").isEmpty()) {
                        product.setPrice(new BigDecimal(csvRecord.get("price")));
                    } else {
                        product.setPrice(BigDecimal.ZERO);
                    }

                    // Safely handle quantity (default to 0 if not found)
                    if (csvRecord.isMapped("quantity") && !csvRecord.get("quantity").isEmpty()) {
                        product.setQuantity(Integer.parseInt(csvRecord.get("quantity")));
                    } else {
                        product.setQuantity(0);
                    }

                    // Safely handle category
                    if (csvRecord.isMapped("category") && !csvRecord.get("category").isEmpty()) {
                        product.setCategory(csvRecord.get("category"));
                    }

                    products.add(product);
                } catch (Exception e) {
                    log.error("Error parsing row {}: {}", csvRecord.getRecordNumber(), e.getMessage());
                }
            }

            productRepository.saveAll(products);
            return products.size();
        }
    }
}