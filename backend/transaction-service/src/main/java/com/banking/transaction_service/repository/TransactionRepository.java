package com.banking.transaction_service.repository;

import com.banking.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    // Existing method (returns everything)
    List<Transaction> findByAccountNumberOrderByTransactionDateDesc(String accountNumber);

    // NEW: Paginated method
    Page<Transaction> findByAccountNumberOrderByTransactionDateDesc(String accountNumber, Pageable pageable);
}