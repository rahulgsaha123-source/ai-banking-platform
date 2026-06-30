package com.banking.payment_service.client;

import com.banking.payment_service.dto.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Points to your new Transaction Service!
@FeignClient(name = "TRANSACTION-SERVICE")
public interface TransactionServiceClient {

    @PostMapping("/api/transactions")
    Object logTransaction(@RequestBody TransactionRequest request);
}