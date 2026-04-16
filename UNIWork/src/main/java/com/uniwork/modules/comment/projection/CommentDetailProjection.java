package com.uniwork.modules.comment.projection;

import java.time.LocalDateTime;

public interface CommentDetailProjection {
    Long getCommentId();
    Long getTaskId();

    Long getPosterId();
    Long getAuthorId();

    String getContent();

    LocalDateTime getCreatedDate();
    LocalDateTime getUpdatedDate();

    String getAuthorName();
}
