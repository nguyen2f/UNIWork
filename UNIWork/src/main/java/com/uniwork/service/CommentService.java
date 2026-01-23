package com.uniwork.service;

import com.uniwork.model.dto.CommentDTO;
import com.uniwork.model.entity.Comment;
import com.uniwork.model.request.AddComment;

import java.util.List;

public interface CommentService {

    Comment addComment(Long userId, AddComment addComment);

    List<CommentDTO> getAllCommentDTO(Long taskId);
}
