package com.uniwork.modules.comment.repository;

import com.uniwork.modules.comment.entity.Comment;
import com.uniwork.modules.comment.projection.CommentDetailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTaskId(Long taskId);

    @Query("""
            SELECT
                c.commentId     AS commentId,
                c.taskId        AS taskId,
                c.posterId      AS posterId,
                c.authorId      AS authorId,
                c.content       AS content,
                c.createdDate   AS createdDate,
                c.updatedDate   AS updatedDate,
                u.name      AS authorName
            FROM Comment c
            LEFT JOIN User u ON c.authorId = u.userId
            WHERE c.taskId = :taskId
            ORDER BY c.createdDate ASC
            """)
    List<CommentDetailProjection> findCommentDetailByTaskId(Long taskId);

}
