package com.banking.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// This tells Spring to resolve the service name through Eureka and load balancing.
@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerServiceClient {

    @GetMapping("/api/customers/{username}")
    Object getCustomerProfile(@PathVariable("username") String username);
}