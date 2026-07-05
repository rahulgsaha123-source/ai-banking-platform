package com.banking.ai_service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai") // This works perfectly with the Gateway's StripPrefix=1
public class AiController {

    private final ChatClient chatClient;

    public AiController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/ask")
    public String askAI(@RequestParam(value = "question", defaultValue = "Say hello to my new banking app!") String question) {
        return chatClient.prompt()
                // 1. Give the AI its identity and fallback instructions (using account 'a')
                .system("You are a professional, friendly AI Banking Assistant. When a user asks about their balance, you MUST use the getAccountBalance tool to fetch it. If the user doesn't provide an account ID, assume their username is 'a'. Keep your responses helpful and concise.")
                
                // 2. Pass in the actual question from your React frontend
                .user(question)
                
                // 3. Give the AI permission to use the tool you built in BankingTools.java
                .functions("getAccountBalance") 
                
                // 4. Execute the call to Llama 3 / Groq
                .call()
                .content();
    }
}