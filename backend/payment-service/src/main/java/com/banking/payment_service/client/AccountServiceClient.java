package com.banking.payment_service.client;

import com.banking.payment_service.dto.AccountDto;
import com.banking.payment_service.dto.AmountRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Pointing directly to your Account Service!
@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountServiceClient {

   @PutMapping("/api/accounts/{accountNumber}/debit")
    ResponseEntity<AccountDto> debitAccount(@PathVariable("accountNumber") String accountNumber, @RequestBody AmountRequest request);

   @PutMapping("/api/accounts/{accountNumber}/credit")
    ResponseEntity<AccountDto> creditAccount(@PathVariable("accountNumber") String accountNumber, @RequestBody AmountRequest request);
}