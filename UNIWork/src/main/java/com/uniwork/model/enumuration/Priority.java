package com.uniwork.model.enumuration;

public enum Priority {
    LOW(0),
    MEDIUM(1),
    HIGH(2),
    CRITICAL(3);
    private final Integer code;
    Priority(Integer code) {
        this.code = code;
    }
    public Integer getCode() {
        return code;
    }
    public static Priority fromCode(Integer code) {
        for (Priority status : Priority.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
