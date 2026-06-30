package com.banking.account_service.controller;

import com.banking.account_service.dto.AccountRequest;
import com.banking.account_service.dto.AmountRequest;
import com.banking.account_service.entity.Account;
import com.banking.account_service.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountRequest request) {
        Account newAccount = accountService.createAccount(request);
        return ResponseEntity.ok(newAccount);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Account>> getUserAccounts(@PathVariable("username") String username) {
        return ResponseEntity.ok(accountService.getAccountsByUsername(username));
    }

    @PutMapping("/{accountNumber}/debit")
    public ResponseEntity<Account> debitAccount(@PathVariable("accountNumber") String accountNumber, @RequestBody AmountRequest request) {
        return ResponseEntity.ok(accountService.debitAccount(accountNumber, request.getAmount()));
    }

    @PutMapping("/{accountNumber}/credit")
    public ResponseEntity<Account> creditAccount(@PathVariable("accountNumber") String accountNumber, @RequestBody AmountRequest request) {
        return ResponseEntity.ok(accountService.creditAccount(accountNumber, request.getAmount()));
    }
}
