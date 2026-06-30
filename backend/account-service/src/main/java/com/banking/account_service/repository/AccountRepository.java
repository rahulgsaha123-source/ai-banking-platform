package com.banking.account_service.repository;

import com.banking.account_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // A user might have multiple accounts (e.g., a checking AND a savings account)
    List<Account> findByUsername(String username);

    // For when we need to find a specific account by its unique number
    boolean existsByAccountNumber(String accountNumber);

    // Add this to find accounts by their 10-digit number
    java.util.Optional<Account> findByAccountNumber(String accountNumber);
}
