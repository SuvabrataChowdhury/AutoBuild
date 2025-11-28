package com.autobuild.pipeline.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Response DTO for authentication containing JWT token and user info.
 * @author Baibhab Dey
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
}