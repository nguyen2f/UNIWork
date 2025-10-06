package com.uniwork.repository;

import com.uniwork.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    ProjectMember findByProjectId(Long projectId);

    List<ProjectMember> findAllByProjectId(Long projectId);

    ProjectMember findByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findAllByUserId(Long userId);
}
