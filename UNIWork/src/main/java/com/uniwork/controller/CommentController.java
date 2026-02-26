package com.uniwork.controller;

import com.uniwork.model.dto.CommentDTO;
import com.uniwork.model.entity.Comment;
import com.uniwork.model.request.AddCommentRequest;
import com.uniwork.model.response.ResponseFactory;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/comment")
@RestController
@Slf4j
@PreAuthorize("hasAuthority('PERM_MANAGE_COMMENTS')")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity addComment(@RequestAttribute(required = false) Payload payload, @RequestBody AddCommentRequest addCommentRequest) {
        Comment comment =  commentService.addComment(payload.getUserId(), addCommentRequest);
        return ResponseFactory.success(comment);
    }

    @GetMapping("/task/{taskId}" )
    public ResponseEntity getCommentsByTaskId(@RequestAttribute(required = false) Payload payload, @PathVariable Long taskId) {
        List<CommentDTO> comments = commentService.getAllCommentDTO(taskId);
        return ResponseFactory.success(comments);
    }

}
