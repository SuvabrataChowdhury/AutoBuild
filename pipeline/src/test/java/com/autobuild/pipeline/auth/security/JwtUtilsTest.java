package com.autobuild.pipeline.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private final String secret = "mySuperSecretKeyForJwtTesting1234567890"; 
    private final long expirationMs = 3600000;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = new JwtUtils();

        // Inject @Value fields using reflection
        setField(jwtUtils, "jwtSecret", secret);
        setField(jwtUtils, "jwtExpirationMs", expirationMs);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = JwtUtils.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtils.generateToken("john");

        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals("john", jwtUtils.extractUsername(token));
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtils.generateToken("alice");
        String username = jwtUtils.extractUsername(token);

        assertEquals("alice", username);
    }

    @Test
    void testValidateToken_Valid() {
        String token = jwtUtils.generateToken("sam");
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_Expired() throws Exception {
        long expired1ms = -1000;
        setField(jwtUtils, "jwtExpirationMs", expired1ms);

        String token = jwtUtils.generateToken("expiredUser");

        assertFalse(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_Malformed() {
        assertFalse(jwtUtils.validateToken("this-is-not-a-jwt"));
    }
}
