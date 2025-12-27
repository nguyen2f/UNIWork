package com.uniwork.repository;

import com.uniwork.model.enumuration.Priority;
import com.uniwork.model.enumuration.TaskStatus;
import com.uniwork.model.entity.Task;
import com.uniwork.model.projection.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    List<Task> findAllByAssignedToAndStatusIn(Long assignedTo, List<TaskStatus> statuses);

    @Query(value = "SELECT COUNT(*) FROM tasks WHERE assigned_to = :assignedTo AND status = :status AND updated_date < due_date", nativeQuery = true)
    Long countTasksCompletedBeforeDeadline(@Param("assignedTo") Long assignedTo, @Param("status") TaskStatus status);

    Long countByAssignedToAndUpdatedDateBetweenAndStatus(Long assignedTo, LocalDateTime startDate, LocalDateTime endDate, TaskStatus status);

    @Query("""
            SELECT 
                t.projectId AS projectId,
                p.name AS name,
                COUNT(DISTINCT pm.userId) AS totalMembers,
                COUNT(t.taskId) AS totalTasks,
                SUM(CASE WHEN t.status = 3 THEN 1 ELSE 0 END) AS completedTasks,
                SUM(CASE WHEN t.status = 2 THEN 1 ELSE 0 END) AS reviewingTasks,
                SUM(CASE WHEN t.status = 4 THEN 1 ELSE 0 END) AS cancelledTasks,
                SUM(CASE WHEN t.status = 0 THEN 1 ELSE 0 END) AS pendingTasks,
                SUM(CASE WHEN t.status = 1 THEN 1 ELSE 0 END) AS doingTasks
            FROM Task t
            JOIN ProjectMember pm ON t.projectId = pm.projectId
            JOIN Project p ON t.projectId = p.projectId
            WHERE t.projectId IN :projectIds
            GROUP BY t.projectId, p.name
            """)
    List<ReportProjectProjection> reportProjects(@Param("projectIds") List<Long> projectIds);

    @Query("""
            SELECT 
                    COUNT(t.taskId) AS totalTasks,
                    SUM(CASE WHEN t.status = 3 THEN 1 ELSE 0 END) AS completedTasks,
                    SUM(CASE WHEN t.status = 0 THEN 1 ELSE 0 END) AS pendingTasks,
                    SUM(CASE WHEN t.status = 2 THEN 1 ELSE 0 END) AS reviewingTasks,
                    SUM(CASE WHEN t.status = 4 THEN 1 ELSE 0 END) AS cancelledTasks,
                    SUM(CASE WHEN t.status = 1 THEN 1 ELSE 0 END) AS doingTasks
                FROM Task t
                WHERE t.assignedTo = :userId""")
    ReportTaskProjection getTaskReport(@Param("userId") Long userId);

    @Query("""
                SELECT
                    COUNT(t.taskId) AS totalTasks,
                    COALESCE(SUM(
                        CASE WHEN 
                            t.status = 3 AND t.updatedDate <= t.dueDate
                        THEN 1 ELSE 0 END
                    ), 0) AS completedBeforeDeadline
                FROM Task t
                WHERE t.assignedTo = :userId
            """)
    ReportTaskPerformanceProjection getTasksPerformance(@Param("userId") Long userId);

    @Query("""
                SELECT
                    COUNT(t.taskId) FILTER (WHERE t.assignedTo = :userId AND t.status = 3) AS completedTasks,
                    COUNT(t.taskId) FILTER (WHERE t.assignedTo = :userId AND t.status = 3 AND t.updatedDate BETWEEN :startOfWeek AND :now) AS newTasksThisWeek,
                    COUNT(t.taskId) FILTER (WHERE t.assignedTo = :userId AND t.status = 0) AS pendingTasks,
                    COUNT(t.taskId) FILTER (WHERE t.assignedTo = :userId AND t.status = 0 AND t.updatedDate BETWEEN :startOfLastWeek AND :endOfLastWeek) AS pendingTasksLastWeek
                FROM Task t
            """)
    ReportTaskStatsProjection getTaskStats(@Param("userId") Long userId,
                                           @Param("startOfWeek") LocalDateTime startOfWeek,
                                           @Param("now") LocalDateTime now,
                                           @Param("startOfLastWeek") LocalDateTime startOfLastWeek,
                                           @Param("endOfLastWeek") LocalDateTime endOfLastWeek);


    @Query("""
            SELECT 
                t.taskId        AS taskId,
                    t.projectId     AS projectId,
                    t.assignedTo    AS assignedTo,
                    t.createdBy     AS createdBy,
                    t.title         AS title,
                    t.description   AS description,
                    t.priority      AS priority,
                    t.status        AS status,
                    t.dueDate       AS dueDate,
                    t.createdDate  AS createdDate,
                    t.updatedDate  AS updatedDate,
                    t.completed     AS completed,
                    t.tags          AS tags,
                    t.parentId      AS taskParentId,
                u.name      AS assigneeName
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            WHERE t.projectId = :projectId
            """)
    List<TaskDetailProjection> findByProjectIdWithUser(Long projectId);

    @Query("""
            SELECT 
                t.taskId        AS taskId,
                                               t.projectId     AS projectId,
                                               t.assignedTo    AS assignedTo,
                                               t.createdBy     AS createdBy,
                                               t.title         AS title,
                                               t.description   AS description,
                                               t.priority      AS priority,
                                               t.status        AS status,
                                               t.dueDate       AS dueDate,
                                               t.createdDate  AS createdDate,
                                               t.updatedDate  AS updatedDate,
                                               t.completed     AS completed,
                                               t.tags          AS tags,
                                               t.parentId      AS taskParentId,
                u.name      AS assigneeName
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            WHERE t.taskId = :taskId
            """)
    TaskDetailProjection findByTaskId(@Param("taskId") Long taskId);

    @Query("""
            SELECT 
                t.taskId        AS taskId,
                                               t.projectId     AS projectId,
                                               t.assignedTo    AS assignedTo,
                                               t.createdBy     AS createdBy,
                                               t.title         AS title,
                                               t.description   AS description,
                                               t.priority      AS priority,
                                               t.status        AS status,
                                               t.dueDate       AS dueDate,
                                               t.createdDate  AS createdDate,
                                               t.updatedDate  AS updatedDate,
                                               t.completed     AS completed,
                                               t.tags          AS tags,
                                               t.parentId      AS taskParentId,
                u.name      AS assigneeName
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            WHERE t.parentId = :taskParentId
            """)
    List<TaskDetailProjection> findByParentId(@Param("taskParentId") Long taskParentId);

}
