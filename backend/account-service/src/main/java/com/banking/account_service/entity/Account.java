package com.banking.account_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The unique 10-digit bank account number
    @Column(nullable = false, unique = true)
    private String accountNumber;

    // Links this account to the user from the Auth/Customer services
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private BigDecimal balance;

    // e.g., SAVINGS or CHECKING
    @Column(nullable = false)
    private String accountType;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
