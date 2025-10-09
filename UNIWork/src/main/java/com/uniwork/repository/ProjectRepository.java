package com.uniwork.repository;


import com.uniwork.entity.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findProjectByProjectId(Long projectId);

    Project findByProjectId(Long projectId);


}
