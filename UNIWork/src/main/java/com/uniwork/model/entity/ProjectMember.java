package com.uniwork.model.entity;

import com.uniwork.model.enumuration.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@Table(name = "project_members")
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
