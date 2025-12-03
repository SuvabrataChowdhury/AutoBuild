package com.autobuild.pipeline.auth.controller;

import com.autobuild.pipeline.auth.dto.AuthResponse;
import com.autobuild.pipeline.auth.dto.LoginRequest;
import com.autobuild.pipeline.auth.dto.RegisterRequest;
import com.autobuild.pipeline.auth.dto.CurrentUserResponse;
import com.autobuild.pipeline.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AuthController.
 * Uses @WebMvcTest for lightweight controller testing with mocked dependencies.
 * Includes a minimal security configuration for testing.
 * 
 * @author Baibhab Dey
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {AuthController.class, AuthControllerTest.TestSecurityConfig.class})
class AuthControllerTest {

    /**
     * Minimal security configuration for testing that permits all requests.
     */
    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse();
        response.setUsername("john");
        response.setToken("jwt-token-12345");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/user/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.token").value("jwt-token-12345"));
    }

    @Test
    void testRegister_InvalidInput() throws Exception {
        // Arrange - Missing required fields
        String invalidJson = "{\"username\":\"\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/user/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse();
        response.setUsername("john");
        response.setToken("jwt-token-67890");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/user/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.token").value("jwt-token-67890"));
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        // Arrange
        CurrentUserResponse userResponse = new CurrentUserResponse("john", "john@example.com");
        when(authService.getCurrentUser(eq("john"))).thenReturn(userResponse);

        // Create a mock Authentication object
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("john");

        // Act & Assert
        mockMvc.perform(get("/api/v1/user/auth/currentuser")
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetCurrentUser_DifferentUser() throws Exception {
        // Arrange
        CurrentUserResponse userResponse = new CurrentUserResponse("jane", "jane@example.com");
        when(authService.getCurrentUser(eq("jane"))).thenReturn(userResponse);

        // Create a mock Authentication object
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("jane");

        // Act & Assert
        mockMvc.perform(get("/api/v1/user/auth/currentuser")
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }
}