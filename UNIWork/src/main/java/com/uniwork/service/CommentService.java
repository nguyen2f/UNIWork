package com.uniwork.service;

import com.uniwork.model.dto.CommentDTO;
import com.uniwork.model.entity.Comment;
import com.uniwork.model.request.AddComment;
import com.uniwork.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment addComment(Long userId, AddComment addComment) {
        Comment comment = new Comment();
        comment.setAuthorId(userId);
        comment.setPosterId(addComment.getPosterId());
        comment.setTaskId(addComment.getTaskId());
        comment.setContent(addComment.getContent());
        comment.setCreatedDate(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getAllComment(Long taskId) {
        return commentRepository.findAllByTaskId(taskId);
    }

    public List<CommentDTO> getAllCommentDTO(Long taskId) {
        List<CommentDTO> comments = commentRepository.findCommentDetailByTaskId(taskId)
                .stream()
                .map(CommentDTO::new)
                .toList();

        return comments;
    }
}
