package com.uniwork.model.enumuration;

public enum Role {
    OWNER(0),
    PROJECT_MANAGER(10),
    MEMBER(1);

    private final Integer code;

    Role(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }

    public static Role fromCode(Integer code) {
        for (Role r : Role.values()) {
            if (r.code.equals(code)) {
                return r;
            }
        }
        return null;
    }


}
