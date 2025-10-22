package com.autobuild.pipeline.exceptions.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;

import jakarta.persistence.EntityNotFoundException;

public class GlobalPipelineControllerHandlerTest {
    private GlobalPipelineControllerHandler globalHandler = new GlobalPipelineControllerHandler();

    @Test
    void testHandleDuplicateEntryException() {
        DuplicateEntryException exception = new DuplicateEntryException("Dummy Exception");

        ErrorResponse errorResponse = globalHandler.handleDuplicateEntryException(exception);

        assertEquals(HttpStatus.CONFLICT, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody().getDetail());
    }

    @Test
    void testHandleEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Dummy Exception");

        ErrorResponse errorResponse = globalHandler.handleEntityNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody().getDetail());
    }

    @Test
    void testHandleInvalidIdException() {
        InvalidIdException exception = new InvalidIdException("Dummy Exception");

        ErrorResponse errorResponse = globalHandler.handleInvalidIdException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody().getDetail());
    }
}
