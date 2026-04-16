package com.uniwork.modules.project.request;

import lombok.Data;

@Data
public class RemoveMemberRequest {
    private Long projectId;
    private Long memberId;
}
