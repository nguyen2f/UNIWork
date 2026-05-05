package com.uniwork.enums;

public enum IssueStatus {
    OPEN(0),
    IN_PROGRESS(1),
    RESOLVED(2),
    CLOSED(3),
    REOPENED(4);

    private final Integer code;

    IssueStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static IssueStatus fromCode(Integer code) {
        for (IssueStatus status : IssueStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
