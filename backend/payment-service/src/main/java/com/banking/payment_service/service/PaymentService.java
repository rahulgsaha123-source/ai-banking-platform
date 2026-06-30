package com.banking.payment_service.service;

import com.banking.payment_service.client.AccountServiceClient;
import com.banking.payment_service.client.TransactionServiceClient;
import com.banking.payment_service.dto.AccountDto;
import com.banking.payment_service.dto.AmountRequest;
import com.banking.payment_service.dto.PaymentCompletedEvent;
import com.banking.payment_service.dto.TransactionRequest;
import com.banking.payment_service.dto.TransferRequest;
import com.banking.payment_service.entity.Payment;
import com.banking.payment_service.entity.PaymentStatus;
import com.banking.payment_service.repository.PaymentRepository;

// ✅ 1. Add these Resilience4j imports
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountServiceClient accountServiceClient;
    private final TransactionServiceClient transactionServiceClient; 
    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;
    
    public PaymentService(PaymentRepository paymentRepository, 
                          AccountServiceClient accountServiceClient, 
                          TransactionServiceClient transactionServiceClient,
                          KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.accountServiceClient = accountServiceClient;
        this.transactionServiceClient = transactionServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    // ✅ 2. Add the annotations here. The names must match exactly what you put in application.yml
    @Retry(name = "accountRetry")
    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackTransfer")
    @RateLimiter(name = "paymentLimiter")
    @Transactional
    public Payment processTransfer(TransferRequest request) {
        Payment payment = Payment.builder()
                .referenceNumber("TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .fromAccount(request.getFromAccount())
                .toAccount(request.getToAccount())
                .amount(request.getAmount())
                .status(PaymentStatus.INITIATED)
                .build();
        
        payment = paymentRepository.save(payment);

        try {
            AmountRequest amountRequest = new AmountRequest(request.getAmount());

            AccountDto senderAccount = accountServiceClient.debitAccount(request.getFromAccount(), amountRequest).getBody();
            AccountDto receiverAccount = accountServiceClient.creditAccount(request.getToAccount(), amountRequest).getBody();

            transactionServiceClient.logTransaction(TransactionRequest.builder()
                    .referenceNumber(payment.getReferenceNumber())
                    .accountNumber(request.getFromAccount())
                    .amount(request.getAmount())
                    .transactionType("TRANSFER_OUT")
                    .balanceAfter(senderAccount.getBalance())
                    .remarks("Transfer to " + request.getToAccount())
                    .build());

            transactionServiceClient.logTransaction(TransactionRequest.builder()
                    .referenceNumber(payment.getReferenceNumber())
                    .accountNumber(request.getToAccount())
                    .amount(request.getAmount())
                    .transactionType("TRANSFER_IN")
                    .balanceAfter(receiverAccount.getBalance())
                    .remarks("Transfer from " + request.getFromAccount())
                    .build());

            payment.setStatus(PaymentStatus.SUCCESS);
            
            // 3. FIRE THE KAFKA EVENT!
            PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                    .referenceNumber(payment.getReferenceNumber())
                    .fromAccount(request.getFromAccount())
                    .toAccount(request.getToAccount())
                    .amount(request.getAmount())
                    .build();
            
            kafkaTemplate.send("payment-completed-topic", event);

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            // This exception being thrown is what triggers the Circuit Breaker!
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }

        return paymentRepository.save(payment);
    }

    // ✅ 3. Create the Fallback Method
    // Rules: Must have the exact same return type (Payment) and parameters (TransferRequest), plus a Throwable.
    public Payment fallbackTransfer(TransferRequest request, Throwable throwable) {
        // Check if it was the Rate Limiter that stopped it
        if (throwable instanceof RequestNotPermitted) {
            System.err.println("🚦 RATE LIMITER TRIGGERED: Too many requests!");
            return Payment.builder()
                    .referenceNumber("BLOCKED")
                    .fromAccount(request.getFromAccount())
                    .toAccount(request.getToAccount())
                    .amount(request.getAmount())
                    .status(PaymentStatus.FAILED)
                    .build();
        }

        // Otherwise, it was a normal network failure/circuit breaker
        System.err.println("🛡️ CIRCUIT BREAKER TRIGGERED: Downstream service down! Reason: " + throwable.getMessage());
        return Payment.builder()
                .referenceNumber("UNAVAILABLE")
                .fromAccount(request.getFromAccount())
                .toAccount(request.getToAccount())
                .amount(request.getAmount())
                .status(PaymentStatus.FAILED)
                .build();
    }

}