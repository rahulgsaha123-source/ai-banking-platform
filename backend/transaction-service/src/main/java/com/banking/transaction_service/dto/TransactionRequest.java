package com.banking.transaction_service.dto;

import com.banking.transaction_service.entity.TransactionType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String referenceNumber;
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private BigDecimal balanceAfter;
    private String remarks;
}