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
    TASK_ACCESS_DENIED("TASK_002", "You are not allowed to view this task", HttpStatus.FORBIDDEN),

    STAGE_NOT_FOUND("STAGE_001", "Stage not found", HttpStatus.NOT_FOUND),
    STAGE_INVALID_STATE("STAGE_002", "Stage is in an invalid state for this operation", HttpStatus.BAD_REQUEST),
    STAGE_WATERFALL_SEQUENCE("STAGE_003", "Previous phases must be completed first", HttpStatus.BAD_REQUEST),
    STAGE_HAS_UNDONE_TASKS("STAGE_004", "All tasks must be completed before completing the stage", HttpStatus.BAD_REQUEST),
    STAGE_HAS_TASKS("STAGE_005", "Stage still has tasks, move them before deleting", HttpStatus.BAD_REQUEST),

    PROJECT_NOT_FOUND("PRJ_001", "Project not found", HttpStatus.NOT_FOUND);



    private final String code;
    private final String message;
    private final HttpStatus status;

}
