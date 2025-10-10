package com.uniwork.service;

import com.uniwork.entity.model.Comment;
import com.uniwork.entity.request.AddComment;
import com.uniwork.repository.CommentRepository;
import org.hibernate.annotations.Comments;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public ResponseEntity addComment(Long userId, AddComment addComment) {
        Comment comment = new Comment();
        comment.setAuthorId(userId);
        comment.setPosterId(addComment.getPosterId());
        comment.setTaskId(addComment.getTaskId());
        comment.setContent(addComment.getContent());
        comment.setCreatedDate(new Date());
        return ResponseEntity.ok(commentRepository.save(comment));
    }

    public List<Comment> getAllComment(Long taskId) {
        return commentRepository.findAllByTaskId(taskId);
    }
}
