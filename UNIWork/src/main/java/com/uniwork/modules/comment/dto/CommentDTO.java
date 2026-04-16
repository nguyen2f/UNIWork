package com.uniwork.modules.comment.dto;

import com.uniwork.modules.comment.projection.CommentDetailProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long commentId;
    private Long taskId;

    private Long posterId;
    private Long authorId;

    private String content;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private String authorName;

    public CommentDTO(CommentDetailProjection p) {
        this.commentId = p.getCommentId();
        this.taskId = p.getTaskId();
        this.posterId = p.getPosterId();
        this.authorId = p.getAuthorId();
        this.content = p.getContent();
        this.createdDate = p.getCreatedDate();
        this.updatedDate = p.getUpdatedDate();
        this.authorName = p.getAuthorName();
    }
}

