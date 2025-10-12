package com.uniwork.entity.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private Long taskId;
    private Long posterId;
    private Long authorId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
