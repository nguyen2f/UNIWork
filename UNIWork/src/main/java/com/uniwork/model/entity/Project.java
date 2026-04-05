package com.uniwork.model.entity;

import com.uniwork.model.enumuration.Priority;
import com.uniwork.model.enumuration.ProjectMethod;
import com.uniwork.model.enumuration.ProjectStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "projects")
@SQLRestriction("is_deleted = false")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;
    @Enumerated(EnumType.STRING)
    private ProjectMethod method; // AGILE, WATERFALL, or STANDARD
    private String name;
    private String description;
    @Column(name = "priority")
    private Priority priority;
    private String category;
    private String client;
    private String department;
    private String riskLevel;
    private Long ownerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Column(name = "status")
    private ProjectStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long departmentId;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;
}
