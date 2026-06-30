package com.banking.account_service.service;

import com.banking.account_service.client.CustomerServiceClient;
import com.banking.account_service.dto.AccountRequest;
import com.banking.account_service.entity.Account;
import com.banking.account_service.repository.AccountRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerServiceClient customerServiceClient;

    public AccountService(AccountRepository accountRepository, CustomerServiceClient customerServiceClient) {
        this.accountRepository = accountRepository;
        this.customerServiceClient = customerServiceClient;
    }

    public Account createAccount(AccountRequest request) {
        try {
            customerServiceClient.getCustomerProfile(request.getUsername());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Error: Cannot create account. No customer profile found for username: " + request.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("Error: Customer Service is currently unavailable.");
        }

        Account newAccount = Account.builder()
                .username(request.getUsername())
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .accountNumber(generateUniqueAccountNumber())
                .build();

        return accountRepository.save(newAccount);
    }

    public List<Account> getAccountsByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            long randomNum = 1000000000L + (long) (random.nextDouble() * 8999999999L);
            accountNumber = String.valueOf(randomNum);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    public Account debitAccount(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Error: Account not found."));

        // Check if they have enough money!
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Error: Insufficient balance.");
        }

        // Subtract the amount and save
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    public Account creditAccount(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Error: Account not found."));

        // Add the amount and save
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }
}
