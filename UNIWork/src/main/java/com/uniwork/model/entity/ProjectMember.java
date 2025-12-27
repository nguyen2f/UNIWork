package com.uniwork.model.entity;

import com.uniwork.model.enumuration.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "project_members")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long pmId;
    private Long userId;
    private Long projectId;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean status;
}
