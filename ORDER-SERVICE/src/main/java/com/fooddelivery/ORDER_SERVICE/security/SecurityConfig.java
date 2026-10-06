package com.fooddelivery.ORDER_SERVICE.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))

                .authorizeHttpRequests(auth -> auth

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs"
                        ).permitAll()

                        // Create order
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/orders"
                        ).hasAnyAuthority("CUSTOMER", "ADMIN")

                        // ADMIN - view ALL orders
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders"
                        ).hasAuthority("ADMIN")

                        // Get individual order
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders/*"
                        ).authenticated()

                        // Customer's orders
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders/customer/**"
                        ).hasAnyAuthority("CUSTOMER", "ADMIN")

                        // Cancel order
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/orders/**"
                        ).hasAnyAuthority("CUSTOMER", "ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}