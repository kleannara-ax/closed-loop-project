package com.company.module.waste.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Module-Waste 보안 설정
 * - Core SecurityConfig보다 우선 적용 (@Order(1))
 * - /waste-api/**, /waste/**, 정적리소스, 헬스, favicon 공개 허용
 */
@Configuration
public class WasteSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain wastePublicFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(
                "/waste-api/**",
                "/waste", "/waste/**",
                "/css/**", "/js/**", "/images/**",
                "/health", "/favicon.ico", "/error"
            )
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
