package com.uniwork.modules.project.entity;

import com.uniwork.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@Table(name = "project_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_project_member", columnNames = {"projectId", "userId"})
        },
        indexes = {
                @Index(name = "idx_pm_user", columnList = "userId"),
                @Index(name = "idx_pm_project", columnList = "projectId")
        }
)
@SQLRestriction("is_deleted = false")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long pmId;
    private Long userId;
    private Long projectId;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean status;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;
}
