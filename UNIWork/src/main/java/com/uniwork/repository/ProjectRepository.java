package com.uniwork.repository;


import com.uniwork.model.enumuration.Priority;
import com.uniwork.model.enumuration.ProjectStatus;
import com.uniwork.model.entity.Project;
import com.uniwork.model.projection.ReportProjectStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Project findProjectByProjectId(Long projectId);

    @Query("""
                SELECT p FROM Project p
                WHERE p.projectId = :projectId
                  AND (:status IS NULL OR p.status = :status)
                  AND (:priority IS NULL OR p.priority = :priority)
            """)
    Project findProjectByProjectIdAndFilter(
            @Param("projectId") Long projectId,
            @Param("priority") Priority priority,
            @Param("status") ProjectStatus status
    );

    Long countProjectIdByStatusNot(ProjectStatus status);

    Long countByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
                SELECT 
                    COUNT(p.projectId) FILTER (WHERE p.status <> 2) AS activeProjects,
                    COUNT(p.projectId) FILTER (WHERE p.createdDate BETWEEN :startOfMonth AND :now) AS newProjectThisMonth
                FROM Project p
            """)
    ReportProjectStatsProjection getProjectStats(@Param("startOfMonth") LocalDateTime startOfMonth,
                                                 @Param("now") LocalDateTime now);

}
