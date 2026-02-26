package com.uniwork.repository;

import com.uniwork.model.entity.Stage;
import com.uniwork.model.enumuration.StageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface StageRepository extends JpaRepository<Stage, Long> {

    List<Stage> findByProjectIdOrderByOrderIndexAsc(Long projectId);

    Optional<Stage> findByProjectIdAndStatus(Long projectId, StageStatus status);

}
