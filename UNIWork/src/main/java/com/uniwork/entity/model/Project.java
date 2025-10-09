package com.uniwork.entity.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

import java.util.Date;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;
    private String name;
    private String description;
    private String priority;
    private String category;
    private String client;
    private String department;
    private String riskLevel;
    private Long ownerId;
    private Date startDate;
    private Date endDate;
    private String status;
    private Date createdDate;
    private Date updatedDate;
}
