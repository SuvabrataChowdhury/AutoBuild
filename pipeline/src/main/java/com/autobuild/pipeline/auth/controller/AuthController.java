package com.autobuild.pipeline.auth.controller;

import com.autobuild.pipeline.auth.dto.RegisterRequest;
import com.autobuild.pipeline.auth.dto.LoginRequest;
import com.autobuild.pipeline.auth.dto.AuthResponse;
import com.autobuild.pipeline.auth.dto.UserResponse;
import com.autobuild.pipeline.auth.entity.User;
import com.autobuild.pipeline.auth.repository.UserRepository;
import com.autobuild.pipeline.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getEmail()));
    }
}