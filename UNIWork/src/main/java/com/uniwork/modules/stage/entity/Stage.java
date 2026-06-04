package com.uniwork.modules.stage.entity;

import com.uniwork.enums.StageStatus;
import com.uniwork.enums.StageType;
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

    private String description;

    private String goal; // Mục tiêu của stage/sprint/phase

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
