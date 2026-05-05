package com.uniwork.modules.project.request;

import lombok.Data;

@Data
public class AssignMemberRequest {
    private Long userId;
    private String role;
}
