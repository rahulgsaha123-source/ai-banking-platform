package com.banking.ai_service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final ChatClient chatClient;

    public AiController(ChatClient.Builder chatClientBuilder) {
        // 1. We give the AI a "Brain" so it remembers previous messages in the
        // conversation!
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    @GetMapping("/ask")
    public String askAI(@RequestParam(value = "question", defaultValue = "Say hello!") String question) {
        return chatClient.prompt()
                // 2. We give it stricter rules so it stops stalling and just gives the data
                .system("You are a highly efficient, direct AI Banking Assistant. " +
                        "RULE 1: When a user asks for a balance, ALWAYS use the getAccountBalance tool immediately. " +
                        "RULE 2: If the user doesn't specify an account, default to account 'a'. " +
                        "RULE 3: Once you receive the data from the tool, immediately tell the user the balance. Use the ₹ symbol for INR currency. Do not stall or ask follow-up questions.")
                .user(question)
                .functions("getAccountBalance")
                .call()
                .content();
    }
}