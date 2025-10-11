package com.uniwork.repository;


import com.uniwork.entity.enumuration.Priority;
import com.uniwork.entity.enumuration.ProjectStatus;
import com.uniwork.entity.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


}
