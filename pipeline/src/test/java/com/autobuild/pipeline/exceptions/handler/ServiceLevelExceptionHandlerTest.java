package com.autobuild.pipeline.exceptions.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;

import jakarta.validation.ConstraintViolationException;

public class ServiceLevelExceptionHandlerTest {
    private ServiceLevelExceptionHandler globalHandler = new ServiceLevelExceptionHandler();

    @Test
    void testHandleDuplicateEntryException() {
        DuplicateEntryException exception = new DuplicateEntryException("Dummy Exception");

        ErrorResponse errorResponse = globalHandler.handleDuplicateEntryException(exception);

        assertEquals(HttpStatus.CONFLICT, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody().getDetail());
    }

    @Test
    void testHandleInvalidIdException() {
        InvalidIdException exception = new InvalidIdException("Dummy Exception");

        ErrorResponse errorResponse = globalHandler.handleInvalidIdException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody().getDetail());
    }

    @Test
    public void testHandleConstraintViolationException() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);

        ErrorResponse errorResponse = globalHandler.handleConstraintViolationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody().getDetail());
    }

    @Test
    public void testHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult result = mock(BindingResult.class);

        doReturn(result).when(exception).getBindingResult();

        doReturn(List.of(new FieldError("Dummy","dummy","dummy"))).when(result).getFieldErrors();

        ErrorResponse errorResponse = globalHandler.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody().getDetail());
    }
}
