package com.autobuild.pipeline.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestAuthEntryPointTest {

    private RestAuthEntryPoint entryPoint;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthenticationException authException;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        entryPoint = new RestAuthEntryPoint();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        authException = new AuthenticationException("Invalid token") {};

        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);

        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testCommenceWritesUnauthorizedJson() throws Exception {
        entryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        String json = responseWriter.toString().trim();

        assertTrue(json.contains("\"error\":\"Unauthorized\""));
        assertTrue(json.contains("\"message\":\"Invalid token\""));
    }
}
