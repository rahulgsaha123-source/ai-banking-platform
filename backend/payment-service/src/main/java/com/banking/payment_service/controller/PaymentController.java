package com.banking.payment_service.controller;

import com.banking.payment_service.dto.TransferRequest;
import com.banking.payment_service.entity.Payment;
import com.banking.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Payment> transferMoney(@RequestBody TransferRequest request) {
        Payment processedPayment = paymentService.processTransfer(request);
        return ResponseEntity.ok(processedPayment);
    }
}