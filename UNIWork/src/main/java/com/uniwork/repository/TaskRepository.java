package com.uniwork.repository;

import com.uniwork.entity.enumuration.Priority;
import com.uniwork.entity.enumuration.TaskStatus;
import com.uniwork.entity.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProjectId(Long projectId);

    Long countAllByProjectId(Long projectId);

    Long countAllByProjectIdAndStatus(Long projectId, TaskStatus status);

    Long countAllByProjectIdAndCreatedDateAfter(Long projectId, Date createdDate);

    List<Task> findAllByProjectIdAndAssignedTo(Long projectId, Long userId);

    List<Task> findByAssignedToAndStatusIn(Long userId, List<String> statuses);

    Long countAllByAssignedTo(Long userId);

    Long countAllByAssignedToAndStatus(Long userId, TaskStatus status);

    @Query("""
                SELECT t FROM Task t
                WHERE t.assignedTo = :assignedTo
                  AND (:priority IS NULL OR t.priority = :priority)
                  AND (:status IS NULL OR t.status = :status)
            """)
    List<Task> findAllByAssignedToAndFilter(
            @Param("assignedTo") Long assignedTo,
            @Param("priority") Priority priority,
            @Param("status") TaskStatus status
    );

    List<Task> findAllByAssignedToAndStatus(Long assignedTo, TaskStatus status);

    @Query(value = "SELECT COUNT(*) FROM tasks WHERE assigned_to = :assignedTo AND status = :status AND updated_date < due_date", nativeQuery = true)
    Long countTasksCompletedBeforeDeadline(@Param("assignedTo") Long assignedTo, @Param("status") TaskStatus status);
}
