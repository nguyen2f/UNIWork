package com.uniwork.controller;

import com.uniwork.model.entity.Comment;
import com.uniwork.model.request.AddComment;
import com.uniwork.model.response.ResponseFactory;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/comment")
@RestController
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity addComment(@RequestAttribute(required = false) Payload payload, @RequestBody AddComment addComment) {
        Comment comment =  commentService.addComment(payload.getUserId(), addComment);
        return ResponseFactory.success(comment);
    }

    @GetMapping("/task/{taskId}" )
    public ResponseEntity getCommentsByTaskId(@RequestAttribute(required = false) Payload payload, @PathVariable Long taskId) {
        List<Comment> comments = commentService.getAllComment(taskId);
        return ResponseFactory.success(comments);
    }

}
