package com.uniwork.model;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId; // Reference to the project this task belongs to
    private Long assignedTo;
    private Long parentId; // For sub-tasks, reference to the parent task
    private String title;
    private String description;
    private String priority; // e.g., Low, Medium, High
    private String status; // e.g., Not Started, In Progress, Completed
    private Date dueDate;
    private Date createdDate;
    private Date updatedDate;

    // Additional fields can be added as needed
}
