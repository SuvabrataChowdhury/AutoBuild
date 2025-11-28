package com.autobuild.pipeline.auth.security;

import com.autobuild.pipeline.auth.dto.AuthResponse;
import com.autobuild.pipeline.auth.entity.User;
import com.autobuild.pipeline.auth.repository.UserRepository;
import com.autobuild.pipeline.auth.service.AuthService;
import com.autobuild.pipeline.auth.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    /**
     * Check if PasswordEncoder bean is loaded correctly
     */
    @Test
    void testPasswordEncoderBeanExists() {
        String encoded = passwordEncoder.encode("password");
        assert(passwordEncoder.matches("password", encoded));
    }

    /**
     * Check if AuthenticationManager bean is loaded correctly
     */
    @Test
    void testAuthenticationManagerExists() {
        assert(authenticationManager != null);
    }

    /**
     * Test /auth/login endpoint with POST
     */
    @Test
    void testAuthLoginIsPermitted() throws Exception {
        AuthResponse resp = new AuthResponse();
        resp.setUsername("john");
        resp.setToken("jwt-token");
        
        when(authService.login(any())).thenReturn(resp);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    /**
     * Test /auth/register endpoint is publicly accessible
     */
    @Test
    void testAuthRegisterIsPermitted() throws Exception {
        AuthResponse resp = new AuthResponse();
        resp.setUsername("john");
        resp.setToken("jwt-token");
        
        when(authService.register(any())).thenReturn(resp);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"email\":\"john@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    /**
     * Test H2 console access (if enabled in application.properties)
     */
    @Test
    void testH2ConsoleIsPermitted() throws Exception {
        mockMvc.perform(get("/h2-console/"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test that protected endpoints require authentication
     * This test verifies the security configuration blocks unauthenticated access
     */
    @Test
    void testProtectedEndpointWithoutAuthenticationIsBlocked() throws Exception {
        // Test with an invalid JWT token
        when(jwtUtils.validateToken(any())).thenReturn(false);
        
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test that protected endpoints without any token are blocked
     */
    @Test
    void testProtectedEndpointWithoutTokenIsBlocked() throws Exception {
        // Don't provide any Authorization header at all
        // The filter won't set authentication, so Spring Security should block it
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test that authenticated endpoints work with valid authentication
     */
    @Test
    @WithMockUser(username = "john")
    void testProtectedEndpointWithValidAuthIsAllowed() throws Exception {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }
    
    /**
     * Test with a valid JWT token
     */
    @Test
    void testProtectedEndpointWithValidJwtIsAllowed() throws Exception {
        String validJwt = "valid.jwt.token";
        
        when(jwtUtils.validateToken(validJwt)).thenReturn(true);
        when(jwtUtils.extractUsername(validJwt)).thenReturn("john");
        
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPasswordHash("encodedPassword");
        
        when(userDetailsService.loadUserByUsername("john")).thenReturn(
            org.springframework.security.core.userdetails.User
                .withUsername("john")
                .password("encodedPassword")
                .authorities("ROLE_USER")
                .build()
        );
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + validJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}