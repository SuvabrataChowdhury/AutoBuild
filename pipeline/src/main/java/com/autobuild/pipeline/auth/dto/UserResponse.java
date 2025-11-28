package com.autobuild.pipeline.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for user profile information.
 */

@Data
@AllArgsConstructor
public class UserResponse {
    private String username;
    private String email;
}