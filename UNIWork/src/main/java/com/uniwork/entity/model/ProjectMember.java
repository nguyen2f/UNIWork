package com.uniwork.entity.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String role; // e.g., "developer", "manager", etc.
    private Boolean status;
}
