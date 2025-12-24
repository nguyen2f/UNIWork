package com.uniwork.repository;

import com.uniwork.model.entity.ProjectMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    ProjectMember findByProjectId(Long projectId);

    List<ProjectMember> findAllByProjectId(Long projectId);

    ProjectMember findByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findAllByUserId(Long userId);

    @Query("SELECT pm.projectId FROM ProjectMember pm WHERE pm.userId = :userId")
    List<Long> findProjectIdsByUserId(Long userId);

    Long countUserIdByProjectId(Long projectId);

    Long countByUserId(Long userId);

    @Query("""
        SELECT pm.projectId
        FROM ProjectMember pm
        WHERE pm.userId = :userId
    """)
    Page<Long> findProjectIdsByUserId(Long userId, Pageable pageable);
}
