package com.uniwork.entity.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;
    private Long projectId; // Reference to the project this task belongs to
    private Long assignedTo;
    private Long createdBy; // Reference to the user who created the task
    private Long parentId; // For sub-tasks, reference to the parent task
    private String title;
    private String description;
    private String priority; // e.g., Low, Medium, High
    private String status; // e.g., Not Started, In Progress, Completed
    private Date dueDate;
    private Date createdDate;
    private Date updatedDate;
    private Long updateBy;
    private Boolean completed;
    private String tags; // List of tags associated with the task

    // Additional fields can be added as needed
}
