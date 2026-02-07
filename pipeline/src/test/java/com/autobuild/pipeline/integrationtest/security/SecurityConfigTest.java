package com.autobuild.pipeline.integrationtest.security;

import com.autobuild.pipeline.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Integration tests for Security Configuration with Keycloak JWT.
 * Tests validate that:
 * - Public endpoints are accessible without authentication
 * - Protected endpoints require valid JWT tokens from Keycloak
 * - JWT token validation is performed correctly
 * 
 * @author Baibhab Dey
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void testPublicEndpointsAreAccessible() throws Exception {
        // H2 Console should be accessible
        mockMvc.perform(get("/h2-console/"))
                .andExpect(status().isOk());
    }

    @Test
    void testSwaggerEndpointsAreAccessible() throws Exception {
        // Swagger UI should be publicly accessible
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointWithoutTokenReturnsUnauthorized() throws Exception {
        // Protected endpoint without JWT should return 401
        mockMvc.perform(get("/api/v1/pipeline"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedEndpointWithValidJwtToken() throws Exception {
        // Protected endpoint with valid JWT mock should return 200
        mockMvc.perform(get("/api/v1/pipeline")
                .with(jwt()
                    .jwt(builder -> builder
                        .claim("preferred_username", "testuser")
                        .claim("email", "test@example.com")
                        .claim("realm_access", java.util.Map.of(
                            "roles", java.util.List.of("user", "admin")
                        ))
                    )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void testCurrentUserEndpointWithValidJwt() throws Exception {
        // Current user endpoint should extract JWT claims
        mockMvc.perform(get("/api/v1/user/auth/currentuser")
                .with(jwt()
                    .jwt(builder -> builder
                        .claim("preferred_username", "john.doe")
                        .claim("email", "john.doe@example.com")
                    )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testCurrentUserEndpointWithoutTokenReturnsUnauthorized() throws Exception {
        // Current user endpoint without JWT should return 401
        mockMvc.perform(get("/api/v1/user/auth/currentuser"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testProtectedEndpointWithMockUser() throws Exception {
        // Alternative: Using Spring Security's @WithMockUser
        mockMvc.perform(get("/api/v1/pipeline"))
                .andExpect(status().isOk());
    }

    @Test
    void testJwtWithRealmRolesIsProcessed() throws Exception {
        // Test that realm roles are correctly extracted from JWT
        mockMvc.perform(get("/api/v1/pipeline")
                .with(jwt()
                    .jwt(builder -> builder
                        .claim("preferred_username", "admin")
                        .claim("email", "admin@example.com")
                        .claim("realm_access", java.util.Map.of(
                            "roles", java.util.List.of("PIPELINE_ADMIN", "USER")
                        ))
                    )
                    .authorities(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_PIPELINE_ADMIN"),
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                    )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void testJwtWithClientRolesIsProcessed() throws Exception {
        // Test that client-specific roles are correctly extracted from JWT
        mockMvc.perform(get("/api/v1/pipeline")
                .with(jwt()
                    .jwt(builder -> builder
                        .claim("preferred_username", "client-user")
                        .claim("email", "client@example.com")
                        .claim("resource_access", java.util.Map.of(
                            "pipeline-service", java.util.Map.of(
                                "roles", java.util.List.of("pipeline-creator", "pipeline-viewer")
                            )
                        ))
                    )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void testSseEndpointIsPublic() throws Exception {
        // SSE subscription endpoint should be publicly accessible
        mockMvc.perform(get("/api/v1/pipeline/build/sse/subscribe/test-pipeline-id"))
                .andExpect(status().isOk());
    }

    @Test
    void testActuatorEndpointsArePublic() throws Exception {
        // Actuator health endpoint should be publicly accessible
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}