package com.uniwork.enums;

public enum SystemRole {
    SUPER_ADMIN,
    ADMIN,
    MANAGER,
    EMPLOYEE;

    public String toValue() {
        return this.name();
    }

    // String -> Enum
    public static SystemRole fromValue(String value) {
        return SystemRole.valueOf(value.toUpperCase());
    }
}
