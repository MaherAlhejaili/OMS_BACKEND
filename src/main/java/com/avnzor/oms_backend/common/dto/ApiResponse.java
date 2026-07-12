package com.avnzor.oms_backend.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp,
        Integer status,
        String path,
        Map<String, String> errors
) {

    public static <T> ApiResponse<T> success(T data) {
        return success("Request completed successfully", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                true,
                message,
                data,
                Instant.now(),
                null,
                null,
                null
        );
    }

    public static <T> ApiResponse<T> error(int status, String message, String path) {
        return new ApiResponse<>(
                false,
                message,
                null,
                Instant.now(),
                status,
                path,
                null
        );
    }

    public static <T> ApiResponse<T> error(int status, String message, String path, Map<String, String> errors) {
        return new ApiResponse<>(
                false,
                message,
                null,
                Instant.now(),
                status,
                path,
                errors
        );
    }
}
