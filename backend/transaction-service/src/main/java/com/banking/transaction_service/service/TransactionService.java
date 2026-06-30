package com.banking.transaction_service.service;

import com.banking.transaction_service.dto.TransactionRequest;
import com.banking.transaction_service.entity.Transaction;
import com.banking.transaction_service.repository.TransactionRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Called internally by the Payment Service
    public Transaction logTransaction(TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .referenceNumber(request.getReferenceNumber())
                .accountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .balanceAfter(request.getBalanceAfter()) // Crucial for statements!
                .remarks(request.getRemarks())
                .build();

        return transactionRepository.save(transaction);
    }

    // Called by the user to view their bank statement
    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        return transactionRepository.findByAccountNumberOrderByTransactionDateDesc(accountNumber);
    }

    // Fetch the 10 most recent transactions (Mini Statement)
    public List<Transaction> getMiniStatement(String accountNumber) {
        Pageable topTen = PageRequest.of(0, 10);
        return transactionRepository.findByAccountNumberOrderByTransactionDateDesc(accountNumber, topTen).getContent();
    }

    // Fetch a specific page of transactions
    public Page<Transaction> getPaginatedStatement(String accountNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByAccountNumberOrderByTransactionDateDesc(accountNumber, pageable);
    }
}