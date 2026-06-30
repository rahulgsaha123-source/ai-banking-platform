package com.banking.account_service.dto;

import lombok.Data;

@Data
public class AccountRequest {
    private String username;
    private String accountType; // e.g., SAVINGS or CHECKING
}
