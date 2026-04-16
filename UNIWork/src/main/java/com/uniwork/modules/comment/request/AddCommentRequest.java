package com.uniwork.modules.comment.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddCommentRequest {
    private Long taskId;
    private Long posterId;
    private Long authorId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
