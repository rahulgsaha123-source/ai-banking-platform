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

       // Notice we are injecting the username 5 times now!
        String systemPrompt = String.format("""
                You are a highly secure, professional AI Banking Assistant. 
                The user you are currently speaking to is authenticated as: '%s'.
                
                SECURITY RULE 1: You are STRICTLY FORBIDDEN from accessing the account balance or transactions of ANY user other than '%s'.
                RULE 2: If the user asks for their balance, automatically use '%s' as the accountId.
                RULE 3: If the user asks about recent transactions, use the getRecentTransactions tool with accountId '%s'.
                RULE 4: If the user asks to send or transfer money, you MUST use the transferMoney tool.
                        - ALWAYS use '%s' as the 'fromUsername'.
                        - Extract the recipient's username and the amount from the user's message.
                RULE 5: When a transfer is successful, provide the user with the Reference Number and the amount transferred using the ₹ symbol.
                """, username, username, username, username, username); 

        return chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                // Add the third superpower here!
                .functions("getAccountBalance", "getRecentTransactions", "transferMoney") 
                .call()
                .content();
    }
}