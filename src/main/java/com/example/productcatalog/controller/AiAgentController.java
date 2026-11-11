package com.example.productcatalog.controller;

import com.example.productcatalog.service.ProductAiTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiAgentController {

    private final ChatClient chatClient;
    private final ProductAiTools productAiTools;
    private final VectorStore vectorStore;

    public AiAgentController(ChatClient chatClient, ProductAiTools productAiTools, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.productAiTools = productAiTools;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/ask")
    public String askAgent(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                // 1. RAG: Adds the QuestionAnswerAdvisor to search the VectorStore for product
                // info
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                // 2. MCP/Tools: Gives the AI access to your Java methods
                .tools(productAiTools)
                .call()
                .content();
    }
}