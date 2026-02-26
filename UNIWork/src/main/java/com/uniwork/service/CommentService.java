package com.uniwork.service;

import com.uniwork.model.dto.CommentDTO;
import com.uniwork.model.entity.Comment;
import com.uniwork.model.request.AddCommentRequest;

import java.util.List;

public interface CommentService {

    Comment addComment(Long userId, AddCommentRequest addCommentRequest);

    List<CommentDTO> getAllCommentDTO(Long taskId);
}
