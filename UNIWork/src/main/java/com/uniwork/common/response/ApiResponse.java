package com.uniwork.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private PageMetadata pagination;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                        .success(true)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, PageMetadata pagination) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .data(data)
                .pagination(pagination)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

}
