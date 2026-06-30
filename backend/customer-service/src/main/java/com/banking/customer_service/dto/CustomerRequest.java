package com.banking.customer_service.dto;

import lombok.Data;

@Data
public class CustomerRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
}
