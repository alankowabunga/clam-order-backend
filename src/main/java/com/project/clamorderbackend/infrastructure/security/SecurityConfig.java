//package com.project.clamorderbackend.infrastructure.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
/// **
// * Security configuration
// */
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // 1. 顯式開啟 CORS (需搭配 CorsConfigurationSource Bean)
//                .cors(org.springframework.security.config.Customizer.withDefaults())
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // 允許所有 OPTIONS 請求 (解決 CORS 預檢)
//                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
//                        // 允許錯誤頁面訪問，避免 404/500 變 403
//                        .requestMatchers("/error").permitAll()
//
//                        .requestMatchers("/api/v1/products/**").permitAll() // 建議加上 **
//                        .requestMatchers("/api/v1/order/calculate").permitAll()
//                        .requestMatchers("/api/v1/admin/**").permitAll()
//                        .requestMatchers("/actuator/**").permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//
//                        .anyRequest().authenticated()
//                );
//
//        return http.build();
//    }
//}
//
