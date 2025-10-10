package com.uniwork.entity.request;

import lombok.Data;

@Data
public class RemoveMemberRequest {
    private Long projectId;
    private Long memberId;
}
