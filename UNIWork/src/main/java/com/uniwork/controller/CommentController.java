package com.uniwork.controller;

import com.uniwork.entity.request.AddComment;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/comment")
@RestController
@Slf4j
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add-comment")
    public ResponseEntity addComment(@RequestAttribute Payload payload, @RequestBody AddComment addComment) {
        return commentService.addComment(payload.getUserId(), addComment);
    }

}
