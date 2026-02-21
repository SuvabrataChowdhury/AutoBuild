package com.autobuild.pipeline.auth.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API response wrapper for consistent response format.
 * 
 * @author Baibhab Dey
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String timestamp;
    private int status;
    private String message;
    private T data;
    private String error;
    private String path;
}
