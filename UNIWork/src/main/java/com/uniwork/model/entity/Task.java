package com.uniwork.model.entity;


import com.uniwork.model.enumuration.Priority;
import com.uniwork.model.enumuration.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;
    private Long parentId; // For sub-tasks, reference to the parent task
    private Long projectId; // Reference to the project this task belongs to
    private Long departmentId;
    private Long stageId; // nullable

    private Long assignedTo;
    private Long createdBy;// Reference to the user who created the task
    private Long managedBy; // Reference to the user who is managing the task (could be a team lead or manager)

    private String title;
    private String description;
    @Column(name = "priority")
    private Priority priority; // e.g., Low, Medium, High
    @Column(name = "status")
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private Long updatedBy;
    private Boolean completed;
    private String tags;


}
