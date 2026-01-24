package com.autobuild.pipeline.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Dummy change to test ci

/**
 * Auth-less security configuration for local development.
 * 
 * @author Suvabrata Chowdhury
 */

@Configuration
@Profile("!demo & !test")
public class DefaultLocalSecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurity(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());

        return http.build();
    }
}
