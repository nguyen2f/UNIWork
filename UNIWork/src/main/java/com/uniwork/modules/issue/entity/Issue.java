package com.uniwork.modules.issue.entity;

import com.uniwork.enums.IssueStatus;
import com.uniwork.enums.IssueType;
import com.uniwork.enums.Priority;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "issues")
@SQLRestriction("is_deleted = false")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;

    private Long taskId;       // Reference to the parent task
    private Long projectId;    // Reference to the project (denormalized for easy querying)
    private Long stageId;

    private Long reportedBy;   // User who reported the issue
    private Long assignedTo;   // User who is assigned to fix the issue

    private String title;
    private String description;

    @Column(name = "type")
    private IssueType type;       // BUG, IMPROVEMENT, QUESTION, etc.

    @Column(name = "priority")
    private Priority priority;    // Reuse existing Priority enum

    @Column(name = "status")
    private IssueStatus status;   // OPEN, IN_PROGRESS, RESOLVED, CLOSED, REOPENED

    private LocalDateTime dueDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long updatedBy;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;
}
