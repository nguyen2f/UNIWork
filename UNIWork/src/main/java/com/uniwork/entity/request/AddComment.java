package com.uniwork.entity.request;

import lombok.Data;

import java.util.Date;

@Data
public class AddComment {
    private Long taskId;
    private Long posterId;
    private Long authorId;
    private String content;
    private Date createdDate;
    private Date updatedDate;
}
