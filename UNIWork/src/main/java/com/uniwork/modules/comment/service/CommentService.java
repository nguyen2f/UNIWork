package com.uniwork.modules.comment.service;

import com.uniwork.modules.comment.dto.CommentDTO;
import com.uniwork.modules.comment.entity.Comment;
import com.uniwork.modules.comment.request.AddCommentRequest;

import java.util.List;

public interface CommentService {

    Comment addComment(Long userId, AddCommentRequest addCommentRequest);

    List<CommentDTO> getAllCommentDTO(Long taskId);
}
