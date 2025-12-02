package com.uniwork.model.enumuration;

public enum ProjectStatus {
    PLANNING(0),
    IN_PROGRESS(1),
    ON_HOLD(2),
    COMPLETED(3),
    CANCELLED(4);

    private final Integer code;
    ProjectStatus(Integer code) {
        this.code = code;
    }
    public Integer getCode() {
        return code;
    }
    public static ProjectStatus fromCode(Integer code) {
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
