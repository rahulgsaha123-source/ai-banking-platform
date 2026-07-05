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
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    @GetMapping("/ask")
    public String askAI(
            @RequestParam(value = "question") String question,
            @RequestParam(value = "username", defaultValue = "unknown") String username) { // <-- We now accept the username!

        // We dynamically inject the logged-in user into the AI's brain
        String systemPrompt = String.format("""
                You are a highly secure, professional AI Banking Assistant. 
                The user you are currently speaking to is authenticated as: '%s'.
                
                SECURITY RULE 1: You are STRICTLY FORBIDDEN from accessing the account balance of ANY user other than '%s'.
                RULE 2: If the user asks "What is my balance?" or does not specify an account ID, you MUST automatically use '%s' as the accountId when calling the getAccountBalance tool.
                RULE 3: Immediately return the fetched balance using the ₹ symbol. Do not ask for clarification.
                """, username, username, username);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .functions("getAccountBalance")
                .call()
                .content();
    }
}