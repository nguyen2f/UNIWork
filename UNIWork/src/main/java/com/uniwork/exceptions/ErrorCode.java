package com.uniwork.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_ERROR("CORE_000", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_FAILED("CORE_001", "Validation failed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("CORE_002", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("CORE_003", "Forbidden access", HttpStatus.FORBIDDEN),
    NOT_FOUND("CORE_004", "Resource not found", HttpStatus.NOT_FOUND),

    USER_NOT_FOUND("USR_001", "User not found", HttpStatus.NOT_FOUND),
    TASK_NOT_FOUND("TASK_001", "Task not found", HttpStatus.NOT_FOUND),
    TASK_ACCESS_DENIED("TASK_002", "You are not allowed to view this task", HttpStatus.FORBIDDEN);



    private final String code;
    private final String message;
    private final HttpStatus status;

}
