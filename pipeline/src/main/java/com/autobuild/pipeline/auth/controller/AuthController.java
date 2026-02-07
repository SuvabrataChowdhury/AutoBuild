package com.autobuild.pipeline.auth.controller;

import com.autobuild.pipeline.auth.dto.CurrentUserResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication endpoints using Keycloak JWT tokens.
 * @author Baibhab Dey
 */
@Profile("!default & !basicAuth")
@RestController
@RequestMapping("/api/v1/user/auth")
public class AuthController {

    @GetMapping("/currentuser")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        return ResponseEntity.ok(new CurrentUserResponse(username, email));
    }
}