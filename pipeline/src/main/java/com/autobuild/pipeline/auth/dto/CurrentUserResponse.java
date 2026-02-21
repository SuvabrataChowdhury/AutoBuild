package com.autobuild.pipeline.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing current user information from Keycloak JWT.
 * @author Baibhab Dey
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserResponse {
    private String username;
    private String email;
    private String userId; // Keycloak user ID from 'sub' claim
}