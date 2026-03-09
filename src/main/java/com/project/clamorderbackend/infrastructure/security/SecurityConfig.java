package com.project.clamorderbackend.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/v1/products").permitAll()
                .requestMatchers("/api/v1/order/calculate").permitAll()
                // Admin endpoints - require authentication
                .requestMatchers("/api/v1/admin/**").permitAll() // TODO: Add auth
                // Actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                // Swagger/OpenAPI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
