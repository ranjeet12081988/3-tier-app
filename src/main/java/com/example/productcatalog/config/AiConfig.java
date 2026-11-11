package com.example.productcatalog.config;

import com.example.productcatalog.entity.Product;
import com.example.productcatalog.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AiConfig {

    // 1. Create the AI Chat Client
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    // 2. Create the RAG Vector Store and load products into it
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel, ProductRepository productRepository) {
        // Use an in-memory vector store for local testing
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        // Load all products from your PostgreSQL database
        List<Product> products = productRepository.findAll();

        // Convert them into AI "Documents" so the LLM can read them
        List<Document> documents = products.stream()
                .map(p -> new Document(
                        "Product: " + p.getName() +
                                " | SKU: " + p.getSku() +
                                " | Price: $" + p.getPrice() +
                                " | Stock: " + p.getQuantity() +
                                " | Description: " + p.getDescription()))
                .collect(Collectors.toList());

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }

        return vectorStore;
    }
}