package com.uniwork.controller;

import com.uniwork.entity.model.Comment;
import com.uniwork.entity.request.AddComment;
import com.uniwork.entity.response.ResponseFactory;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/comment")
@RestController
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add-comment")
    public ResponseEntity addComment(@RequestAttribute(required = false) Payload payload, @RequestBody AddComment addComment) {
        Comment comment =  commentService.addComment(payload.getUserId(), addComment);
        return ResponseFactory.success(comment);
    }

}
