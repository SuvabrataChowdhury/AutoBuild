package com.autobuild.pipeline.auth.advice;

import com.autobuild.pipeline.auth.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Wraps all successful API responses in a consistent ApiResponse format.
 * 
 * @author Baibhab Dey
 */
@RestControllerAdvice(basePackages = "com.autobuild.pipeline")
public class ResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getParameterType().equals(ApiResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                   Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                   ServerHttpRequest request, ServerHttpResponse response) {
        
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String path = httpRequest.getRequestURI();
            
            // Skip wrapping for non-API endpoints
            if (path.startsWith("/actuator") || path.startsWith("/h2-console") || 
                path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.contains("/sse/")) {
                return body;
            }
            
            return ApiResponse.success(body, "Request processed successfully", path);
        }
        
        return ApiResponse.success(body, "Request processed successfully", null);
    }
}
