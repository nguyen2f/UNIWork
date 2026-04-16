package com.uniwork.modules.project.request;

import lombok.Data;

@Data
public class AssignMemberRequest {
    private Long projectId;
    private String projectName;
    private Long userId;
    private String role;
    private String email;
}
