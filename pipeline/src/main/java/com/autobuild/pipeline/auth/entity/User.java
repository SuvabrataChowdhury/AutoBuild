package com.autobuild.pipeline.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing application-specific user data.
 * Authentication is handled by Keycloak, this stores additional app data.
 * @author Baibhab Dey
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @Column(name = "keycloak_user_id", nullable = false)
    private String keycloakUserId; // Maps to Keycloak 'sub' claim
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    // Application-specific fields (not for authentication)
    @Column
    private String displayName;
    
    @Column
    private String avatarUrl;
}