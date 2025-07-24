package com.uniwork.repository;


import com.uniwork.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsById(Long id);

    Project findProjectById(Long id);
}
