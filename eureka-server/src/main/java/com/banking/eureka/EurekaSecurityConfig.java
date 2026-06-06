package com.banking.eureka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * بنحمي الـ Eureka Dashboard بـ Basic Auth.
 * لازم نعطّل CSRF عشان الـ Eureka clients يقدروا يسجّلوا نفسهم.
 */
@Configuration
@EnableWebSecurity
public class EurekaSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ضروري نعطّل CSRF عشان Eureka clients بتعمل POST للتسجيل
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/eureka/**")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {});

        return http.build();
    }
}