package com.uniwork.modules.admin.request;

import lombok.Data;

@Data
public class AdminCreateDepartmentRequest {
    private String departmentName;

    private String location;
}
