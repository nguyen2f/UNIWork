package com.uniwork.model.response;

import org.springframework.http.ResponseEntity;

public class ResponseFactory {
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data, "Success"));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> makePagination(T data, PageMetadata pagination) {
        return ResponseEntity.ok(ApiResponse.success(data, pagination));
    }
}

