package com.banking.payment_service.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TransactionRequest {
    private String referenceNumber;
    private String accountNumber;
    private BigDecimal amount;
    private String transactionType; // We can use String, Spring will auto-convert to Enum!
    private BigDecimal balanceAfter;
    private String remarks;
}