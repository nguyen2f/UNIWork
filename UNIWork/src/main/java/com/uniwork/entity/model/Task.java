package com.uniwork.entity.model;


import com.uniwork.entity.enumuration.Priority;
import com.uniwork.entity.enumuration.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
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
    @Column(name = "priority")
    private Priority priority; // e.g., Low, Medium, High
    @Column(name = "status")
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long updateBy;
    private Boolean completed;
    private String tags; // List of tags associated with the task

    // Additional fields can be added as needed
}
