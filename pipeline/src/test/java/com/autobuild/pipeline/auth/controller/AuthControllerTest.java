package com.autobuild.pipeline.auth.controller;

import com.autobuild.pipeline.auth.dto.LoginRequest;
import com.autobuild.pipeline.auth.dto.RegisterRequest;
import com.autobuild.pipeline.auth.dto.AuthResponse;
import com.autobuild.pipeline.auth.entity.User;
import com.autobuild.pipeline.auth.repository.UserRepository;
import com.autobuild.pipeline.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void testRegister() throws Exception {
        AuthResponse resp = new AuthResponse();
        resp.setToken("jwt-token");
        resp.setUsername("john");

        when(authService.register(any(RegisterRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"email\":\"john@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void testLogin() throws Exception {
        AuthResponse resp = new AuthResponse();
        resp.setUsername("john");
        resp.setToken("jwt-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    @WithMockUser(username = "john")
    void testMe() throws Exception {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}