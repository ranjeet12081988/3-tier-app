package com.example.productcatalog.smoke;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SimpleSmokeTest {

    @Test
    public void contextLoads() {
        // Simple test to ensure context loads
        assertThat(true).isTrue();
    }

    @Test
    public void testSpringBootApplication() {
        // Verify the main class exists
        try {
            Class<?> mainClass = Class.forName("com.example.productcatalog.ProductCatalogApplication");
            assertThat(mainClass).isNotNull();
        } catch (ClassNotFoundException e) {
            assertThat(false).isTrue();
        }
    }
}
