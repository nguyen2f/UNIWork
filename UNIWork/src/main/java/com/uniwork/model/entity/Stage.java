package com.uniwork.model.entity;

import com.uniwork.model.enumuration.StageStatus;
import com.uniwork.model.enumuration.StageType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stages")
@SQLRestriction("is_deleted = false")
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stageId;

    private Long projectId;

    private String name;

    @Enumerated(EnumType.STRING)
    private StageType type; // SPRINT, PHASE, or DEFAULT

    private Integer orderIndex; // Thứ tự trong project

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private StageStatus status;
    // PLANNED, ACTIVE, COMPLETED, CANCELLED

    private Boolean active;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;
}
