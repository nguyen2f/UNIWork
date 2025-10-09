package com.uniwork.entity.enumuration;

public enum TaskStatus {
    PENDING(0),
    DOING(1),
    COMPLETED(2);
    private final Integer code;
    TaskStatus(Integer code) {
        this.code = code;
    }
    public Integer getCode() {
        return code;
    }
    public static TaskStatus fromCode(Integer code) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

}
