package com.autobuild.pipeline.integrationtest.security;

import com.autobuild.pipeline.auth.service.AuthService;
import com.autobuild.pipeline.auth.repository.UserRepository;
import com.autobuild.pipeline.auth.security.JwtAuthenticationFilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void testPasswordEncoderBeanExists() {
        assertThat(passwordEncoder).isNotNull();
        String encoded = passwordEncoder.encode("password");
        assertThat(passwordEncoder.matches("password", encoded)).isTrue();
    }

    @Test
    void testAuthenticationManagerExists() {
        assertThat(authenticationManager).isNotNull();
    }

    @Test
    void testAuthRegisterIsPermitted() throws Exception {
        mockMvc.perform(post("/api/v1/user/auth/register")
                        .contentType("application/json")
                        .content("{\"username\":\"test\",\"email\":\"test@test.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testAuthLoginIsPermitted() throws Exception {
        mockMvc.perform(post("/api/v1/user/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"test\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testH2ConsoleIsAccessible() throws Exception {
        mockMvc.perform(get("/h2-console/"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointWithMockedFilter() throws Exception {
        mockMvc.perform(get("/api/v1/user/auth/currentuser"))
                .andExpect(status().isOk());
    }
}