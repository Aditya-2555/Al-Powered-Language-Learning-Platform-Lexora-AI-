package com.lingua.config;

import com.lingua.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice(basePackages = "com.lingua.controller")
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Only wrap if it's not already an ApiResponse
        return returnType.getParameterType() != ApiResponse.class;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
                                      
        if (body instanceof ApiResponse) {
            return body;
        }
        
        // If it's a string, we might face a ClassCastException if we return an ApiResponse directly due to StringHttpMessageConverter.
        // For simplicity in a REST API returning primarily objects/JSON, we bypass wrapping Strings, or we return them as is.
        if (body instanceof String) {
            return body;
        }

        // If the path is an error path (like /error inherently called by Spring), let it pass as is unless filtered.
        if (request.getURI().getPath().contains("/error")) {
            return body;
        }

        return ApiResponse.success(body);
    }
}
