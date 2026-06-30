package com.banking.transaction_service.controller;

import com.banking.transaction_service.dto.TransactionRequest;
import com.banking.transaction_service.entity.Transaction;
import com.banking.transaction_service.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // The endpoint the Payment Service will hit
    @PostMapping
    public ResponseEntity<Transaction> logTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.logTransaction(request));
    }

    // The endpoint a user hits to see their history
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> getAccountStatement(@PathVariable("accountNumber") String accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountNumber));
    }

    // 1. Mini Statement API
    @GetMapping("/account/{accountNumber}/mini-statement")
    public ResponseEntity<List<Transaction>> getMiniStatement(@PathVariable("accountNumber") String accountNumber) {
        return ResponseEntity.ok(transactionService.getMiniStatement(accountNumber));
    }

    // 2. Paginated Statement API
    @GetMapping("/account/{accountNumber}/statement")
    public ResponseEntity<Page<Transaction>> getPaginatedStatement(
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return ResponseEntity.ok(transactionService.getPaginatedStatement(accountNumber, page, size));
    }
}