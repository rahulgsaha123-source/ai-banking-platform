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
                    return new BalanceResponse(request.accountId(), realBalance, "INR");
                } else {
                    return new BalanceResponse(request.accountId(), 0.0, "ACCOUNT_NOT_FOUND");
                }
                
            } catch (Exception e) {
                System.out.println("❌ Error fetching balance: " + e.getMessage());
                return new BalanceResponse(request.accountId(), 0.0, "INR");
            }
        };
    }

    // --- NEW TOOL: RECENT TRANSACTIONS ---

    // 1. Perfect mapping for your JSON response
    public record TransactionDto(
        String id, 
        String referenceNumber, 
        String accountNumber, 
        double amount, 
        String transactionType, 
        double balanceAfter, 
        String remarks, 
        String transactionDate
    ) {}
    
    public record TransactionRequest(String accountId) {}
    public record TransactionResponse(String accountId, java.util.List<TransactionDto> transactions, String status) {}

    // 2. The "Smart" Tool that translates Username -> Account Number -> Transactions
    @Bean
    @Description("Get the recent transaction history for a specific account username")
    public Function<TransactionRequest, TransactionResponse> getRecentTransactions(RestTemplate restTemplate) {
        return request -> {
            System.out.println("🧠 AI is fetching TRANSACTIONS for user: " + request.accountId());
            
            try {
                // STEP A: Fetch the Account Number using the Username
                String accountUrl = "http://ACCOUNT-SERVICE/api/accounts/" + request.accountId();
                AccountDto[] accounts = restTemplate.getForObject(accountUrl, AccountDto[].class);
                
                if (accounts == null || accounts.length == 0) {
                     return new TransactionResponse(request.accountId(), java.util.List.of(), "ACCOUNT_NOT_FOUND");
                }
                
                String realAccountNumber = accounts[0].accountNumber();
                System.out.println("🔗 Translated username '" + request.accountId() + "' to account number: " + realAccountNumber);

                // STEP B: Fetch the Transactions using the real Account Number
                String txnUrl = "http://TRANSACTION-SERVICE/api/transactions/account/" + realAccountNumber;
                TransactionDto[] transactionsArray = restTemplate.getForObject(txnUrl, TransactionDto[].class);
                
                if (transactionsArray != null && transactionsArray.length > 0) {
                    return new TransactionResponse(request.accountId(), java.util.List.of(transactionsArray), "SUCCESS");
                } else {
                    return new TransactionResponse(request.accountId(), java.util.List.of(), "NO_TRANSACTIONS_FOUND");
                }
                
            } catch (Exception e) {
                System.out.println("❌ Error fetching transactions: " + e.getMessage());
                return new TransactionResponse(request.accountId(), java.util.List.of(), "ERROR_FETCHING_DATA");
            }
        };
    }

    // --- NEW TOOL: MONEY TRANSFER ---

    // 1. DTOs to match your Payment Service JSON request and response
    public record PaymentTransferRequest(String fromAccount, String toAccount, double amount) {}
    public record PaymentTransferResponse(Long id, String referenceNumber, String fromAccount, String toAccount, double amount, String status, String createdAt) {}

    // 2. DTOs for the AI to use
    public record AiTransferRequest(String fromUsername, String toUsername, double amount) {}
    public record AiTransferResult(String fromUsername, String toUsername, double amount, String status, String referenceNumber, String message) {}

    // 3. The Smart Transfer Tool
    @Bean
    @Description("Transfer money from the authenticated user to a target user.")
    public Function<AiTransferRequest, AiTransferResult> transferMoney(RestTemplate restTemplate) {
        return request -> {
            System.out.println("💸 AI is initiating TRANSFER from: '" + request.fromUsername() + "' to: '" + request.toUsername() + "' for ₹" + request.amount());
            
            try {
                // STEP A: Translate the Sender's Username to Account Number
                String fromUrl = "http://ACCOUNT-SERVICE/api/accounts/" + request.fromUsername();
                AccountDto[] fromAccounts = restTemplate.getForObject(fromUrl, AccountDto[].class);
                if (fromAccounts == null || fromAccounts.length == 0) {
                     return new AiTransferResult(request.fromUsername(), request.toUsername(), request.amount(), "FAILED", null, "Sender account not found.");
                }
                String fromAccountNumber = fromAccounts[0].accountNumber();

                // STEP B: Translate the Recipient's Username to Account Number
                String toUrl = "http://ACCOUNT-SERVICE/api/accounts/" + request.toUsername();
                AccountDto[] toAccounts = restTemplate.getForObject(toUrl, AccountDto[].class);
                if (toAccounts == null || toAccounts.length == 0) {
                     return new AiTransferResult(request.fromUsername(), request.toUsername(), request.amount(), "FAILED", null, "Recipient account not found.");
                }
                String toAccountNumber = toAccounts[0].accountNumber();

                // STEP C: Fire the POST Request to the Payment Service!
                String paymentUrl = "http://PAYMENT-SERVICE/api/payments/transfer";
                PaymentTransferRequest paymentRequest = new PaymentTransferRequest(fromAccountNumber, toAccountNumber, request.amount());
                
                PaymentTransferResponse paymentResponse = restTemplate.postForObject(paymentUrl, paymentRequest, PaymentTransferResponse.class);
                
                if (paymentResponse != null && "SUCCESS".equals(paymentResponse.status())) {
                    System.out.println("✅ Transfer Successful! Ref: " + paymentResponse.referenceNumber());
                    return new AiTransferResult(request.fromUsername(), request.toUsername(), request.amount(), "SUCCESS", paymentResponse.referenceNumber(), "Transfer completed successfully.");
                } else {
                    return new AiTransferResult(request.fromUsername(), request.toUsername(), request.amount(), "FAILED", null, "Transfer was rejected by the payment service.");
                }
                
            } catch (Exception e) {
                System.out.println("❌ Error during transfer: " + e.getMessage());
                return new AiTransferResult(request.fromUsername(), request.toUsername(), request.amount(), "ERROR", null, "An error occurred while processing the transfer.");
            }
        };
    }
}
