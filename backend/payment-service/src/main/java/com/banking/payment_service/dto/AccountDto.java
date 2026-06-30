package com.banking.payment_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountDto {
    private String accountNumber;
    private BigDecimal balance;
}