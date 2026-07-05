package com.banking.ai_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import java.util.function.Function;

@Configuration
public class BankingTools {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Input/Output records for the AI
    public record BalanceRequest(String accountId) {}
    public record BalanceResponse(String accountId, double balance, String currency) {}

    // NEW: This perfectly maps to your curl JSON output!
    public record AccountDto(Long id, String accountNumber, String username, double balance, String accountType, String createdAt) {}

    @Bean
    @Description("Get the current bank balance for a specific account username or ID")
    public Function<BalanceRequest, BalanceResponse> getAccountBalance(RestTemplate restTemplate) {
        return request -> {
            System.out.println("🧠 AI is fetching REAL balance for account: " + request.accountId());
            
            try {
                // Call the Account Service via Eureka using the exact path from your curl
                String url = "http://ACCOUNT-SERVICE/api/accounts/" + request.accountId();
                
                // We map it to an Array of AccountDto because your API returns [ { ... } ]
                AccountDto[] accounts = restTemplate.getForObject(url, AccountDto[].class);
                
                // If we got a valid response, extract the balance from the first account in the array
                if (accounts != null && accounts.length > 0) {
                    double realBalance = accounts[0].balance();
                    return new BalanceResponse(request.accountId(), realBalance, "USD");
                } else {
                    return new BalanceResponse(request.accountId(), 0.0, "ACCOUNT_NOT_FOUND");
                }
                
            } catch (Exception e) {
                System.out.println("❌ Error fetching balance: " + e.getMessage());
                return new BalanceResponse(request.accountId(), 0.0, "ERROR");
            }
        };
    }
}