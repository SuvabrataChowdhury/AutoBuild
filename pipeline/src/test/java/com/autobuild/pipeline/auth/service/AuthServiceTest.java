package com.autobuild.pipeline.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.autobuild.pipeline.auth.dto.AuthResponse;
import com.autobuild.pipeline.auth.dto.CurrentUserResponse;
import com.autobuild.pipeline.auth.dto.LoginRequest;
import com.autobuild.pipeline.auth.dto.RegisterRequest;
import com.autobuild.pipeline.auth.entity.User;
import com.autobuild.pipeline.auth.repository.UserRepository;
import com.autobuild.pipeline.auth.security.JwtUtils;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("john");
        req.setEmail("john@example.com");
        req.setPassword("secret");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(jwtUtils.generateToken("john")).thenReturn("tok123");

        AuthResponse response = authService.register(req);

        assertEquals("tok123", response.getToken());
        assertEquals("john", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterThrowsWhenUsernameExists() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("john");
        req.setEmail("john@example.com");
        req.setPassword("secret");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterThrowsWhenEmailExists() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("john");
        req.setEmail("john@example.com");
        req.setPassword("secret");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoginSuccess() {
        LoginRequest req = new LoginRequest();
        req.setUsername("john");
        req.setPassword("secret");

        User user = new User(UUID.randomUUID(), "john", "john@example.com", "hashed");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtUtils.generateToken("john")).thenReturn("tok456");

        AuthResponse response = authService.login(req);

        assertEquals("tok456", response.getToken());
        assertEquals("john", response.getUsername());
    }

    @Test
    void testLoginThrowsWhenUserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setUsername("unknown");
        req.setPassword("secret");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }

    @Test
    void testLoginThrowsWhenPasswordWrong() {
        LoginRequest req = new LoginRequest();
        req.setUsername("john");
        req.setPassword("wrong");

        User user = new User(UUID.randomUUID(), "john", "john@example.com", "hashed");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }

    @Test
    void testGetCurrentUserSuccess() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "hashed");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        CurrentUserResponse response = authService.getCurrentUser("john");

        assertEquals("john", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void testGetCurrentUserThrowsWhenNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.getCurrentUser("ghost"));
    }
}
