package com.uniwork.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddComment {
    private Long taskId;
    private Long posterId;
    private Long authorId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
