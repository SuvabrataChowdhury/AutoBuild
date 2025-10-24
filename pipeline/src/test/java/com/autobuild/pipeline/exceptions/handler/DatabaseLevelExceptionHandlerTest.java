package com.autobuild.pipeline.exceptions.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import jakarta.persistence.EntityNotFoundException;

public class DatabaseLevelExceptionHandlerTest {
    private DatabaseLevelExceptionHandler globalHandler = new DatabaseLevelExceptionHandler();

    @Test
    void testHandleEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Dummy Exception");

        ErrorResponse errorResponse = globalHandler.handleEntityNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody().getDetail());
    }
}
