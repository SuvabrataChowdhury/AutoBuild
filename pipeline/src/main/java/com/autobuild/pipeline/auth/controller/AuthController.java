package com.autobuild.pipeline.auth.controller;

import com.autobuild.pipeline.auth.dto.RegisterRequest;
import com.autobuild.pipeline.auth.dto.LoginRequest;
import com.autobuild.pipeline.auth.dto.AuthResponse;
import com.autobuild.pipeline.auth.dto.UserResponse;
import com.autobuild.pipeline.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Custom authentication entry point for REST API.
 * @author Baibhab Dey
 */
@RestController
@RequestMapping("/api/v1/user/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/currentuser")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }
}