package com.autobuild.pipeline.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Global handler for any/all exceptions coming from Pipeline.
 * @author Suvabrata Chowdhury
 */

@Slf4j
@RestControllerAdvice
public class GlobalPipelineControllerHandler {

    @ExceptionHandler(DuplicateEntryException.class)
    public ErrorResponse handleDuplicateEntryException(DuplicateEntryException exception) {
        log.error(exception.getMessage());

        return ErrorResponse.builder(exception, HttpStatus.CONFLICT, exception.getMessage())
                            .property("errorCategory", "Client Error")
                            .build();
    }

    @ExceptionHandler(InvalidIdException.class)
    public ErrorResponse handleInvalidIdException(InvalidIdException exception) {
        log.error(exception.getMessage());

        return ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.getMessage())
                            .property("errorCategory", "Client Error")
                            .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        log.error(exception.getMessage());

        return ErrorResponse.builder(exception, HttpStatus.NOT_FOUND, exception.getMessage())
                            .property("errorCategory", "Client Error")
                            .build();
    }
    
}
