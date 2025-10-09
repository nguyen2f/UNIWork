package com.uniwork.repository;

import com.uniwork.entity.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProjectId(Long projectId);

    Long countAllByProjectId(Long projectId);

    Long countAllByProjectIdAndStatusEqualsIgnoreCase(Long projectId, String status);

    Long countAllByProjectIdAndCreatedDateAfter(Long projectId, Date createdDate);
}
