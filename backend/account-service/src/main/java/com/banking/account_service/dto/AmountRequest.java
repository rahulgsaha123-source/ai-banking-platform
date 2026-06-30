package com.banking.account_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AmountRequest {
    private BigDecimal amount;
}