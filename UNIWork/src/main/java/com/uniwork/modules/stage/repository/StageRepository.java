package com.uniwork.modules.stage.repository;

import com.uniwork.modules.stage.entity.Stage;
import com.uniwork.enums.StageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StageRepository extends JpaRepository<Stage, Long> {

    List<Stage> findByProjectIdOrderByOrderIndexAsc(Long projectId);

    Optional<Stage> findByProjectIdAndStatus(Long projectId, StageStatus status);

    Long countByProjectId(Long projectId);

    List<Stage> findByProjectIdAndActiveTrue(Long projectId);

}
