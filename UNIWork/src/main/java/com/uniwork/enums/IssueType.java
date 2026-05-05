package com.uniwork.enums;

public enum IssueType {
    BUG(0),
    IMPROVEMENT(1),
    QUESTION(2),
    DOCUMENTATION(3),
    OTHER(4);

    private final Integer code;

    IssueType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static IssueType fromCode(Integer code) {
        for (IssueType type : IssueType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
