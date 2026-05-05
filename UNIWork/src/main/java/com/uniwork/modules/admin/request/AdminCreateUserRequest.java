package com.uniwork.modules.admin.request;

import com.uniwork.enums.SystemRole;
import lombok.Data;

@Data
public class AdminCreateUserRequest {
    private String name;
    private String email;
    private String password;
    private Long departmentId;
    private SystemRole systemRole;
}
