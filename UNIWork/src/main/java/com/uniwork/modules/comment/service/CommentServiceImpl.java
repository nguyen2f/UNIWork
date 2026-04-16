package com.uniwork.modules.comment.service;

import com.uniwork.modules.comment.dto.CommentDTO;
import com.uniwork.modules.comment.entity.Comment;
import com.uniwork.modules.comment.request.AddCommentRequest;
import com.uniwork.modules.comment.repository.CommentRepository;
import com.uniwork.modules.comment.service.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment addComment(Long userId, AddCommentRequest addCommentRequest) {
        Comment comment = new Comment();
        comment.setAuthorId(userId);
        comment.setPosterId(addCommentRequest.getPosterId());
        comment.setTaskId(addCommentRequest.getTaskId());
        comment.setContent(addCommentRequest.getContent());
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
