package com.uniwork.enums;

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

    public static ProjectStatus fromProgress(double completedPercent, Long total, Long pending, Long doing) {
        if (total == null || total == 0) {
            return PLANNING;
        }
        if (completedPercent >= 100.0) {
            return COMPLETED;
        }
        if (doing != null) {
            return IN_PROGRESS;
        }
        return ON_HOLD;
    }

}
