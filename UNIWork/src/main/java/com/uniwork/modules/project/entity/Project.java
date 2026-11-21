package com.uniwork.modules.project.entity;

import com.uniwork.enums.Priority;
import com.uniwork.enums.ProjectMethod;
import com.uniwork.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_project_owner", columnList = "ownerId"),
        @Index(name = "idx_project_dept", columnList = "departmentId"),
        @Index(name = "idx_project_status", columnList = "status"),
        @Index(name = "idx_project_dates", columnList = "startDate, endDate")
})
@SQLRestriction("is_deleted = false")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;
    @Enumerated(EnumType.STRING)
    private ProjectMethod method; // AGILE, WATERFALL, or STANDARD
    private String name;
    private String description;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "priority")
    private Priority priority;
    private String category;
    private String client;
    private String department;
    private String riskLevel;
    private Long ownerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private ProjectStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long departmentId;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;
}
