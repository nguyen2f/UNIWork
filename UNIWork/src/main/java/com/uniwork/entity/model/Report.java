package com.uniwork.entity.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Report {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long reportId;
    private Long projectId;
    private String title;
    private String type;
    private String url;
    private LocalDateTime createdDate;

}
