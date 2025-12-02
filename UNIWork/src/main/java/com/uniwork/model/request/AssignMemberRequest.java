package com.uniwork.model.request;

import lombok.Data;

@Data
public class AssignMemberRequest {
    private Long projectId;
    private Long userId;
    private String role;
}
