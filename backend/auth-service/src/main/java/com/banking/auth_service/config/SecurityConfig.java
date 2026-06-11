package com.banking.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF since we will use JWTs later
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register", "/auth/login").permitAll() // Leave these open
                .anyRequest().authenticated() // Lock down everything else
            );
        
        return http.build();
    }
}