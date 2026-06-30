package com.banking.account_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Resolve Customer Service through Eureka rather than hardcoding a URL.
@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerServiceClient {

    @GetMapping("/api/customers/{username}")
    Object getCustomerProfile(@PathVariable("username") String username);
}
