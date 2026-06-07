package com.uniwork.modules.task.repository;

import com.uniwork.enums.Priority;
import com.uniwork.enums.TaskStatus;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.task.projection.*;
import com.uniwork.modules.user.projection.*;
import com.uniwork.modules.chat.projection.*;
import com.uniwork.modules.comment.projection.*;
import com.uniwork.modules.file.projection.*;
import com.uniwork.modules.report.projection.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Task> findAllByAssignedToAndStatusIn(Long assignedTo, List<TaskStatus> statuses, Pageable pageable);


    @Query(value = "SELECT COUNT(*) FROM tasks WHERE assigned_to = :assignedTo AND status = :status AND updated_date < due_date AND is_deleted = false", nativeQuery = true)
    Long countTasksCompletedBeforeDeadline(@Param("assignedTo") Long assignedTo, @Param("status") TaskStatus status);

    Long countByAssignedToAndUpdatedDateBetweenAndStatus(Long assignedTo, LocalDateTime startDate, LocalDateTime endDate, TaskStatus status);

    @Query("""
            SELECT
                p.projectId AS projectId,
                p.name AS name,
                COUNT(DISTINCT pm.userId) AS totalMembers,
                COUNT(t.taskId) AS totalTasks,
                SUM(CASE WHEN t.status = 3 THEN 1 ELSE 0 END) AS completedTasks,
                SUM(CASE WHEN t.status = 2 THEN 1 ELSE 0 END) AS reviewingTasks,
                SUM(CASE WHEN t.status = 4 THEN 1 ELSE 0 END) AS cancelledTasks,
                SUM(CASE WHEN t.status = 0 THEN 1 ELSE 0 END) AS pendingTasks,
                SUM(CASE WHEN t.status = 1 THEN 1 ELSE 0 END) AS doingTasks
            FROM Project p
            JOIN ProjectMember pm ON p.projectId = pm.projectId
            LEFT JOIN Task t ON p.projectId = t.projectId
            WHERE p.projectId IN :projectIds
            GROUP BY p.projectId, p.name
            """)
    List<ReportProjectProjection> reportProjects(@Param("projectIds") List<Long> projectIds);

    @Query("""
        SELECT 
            COUNT(t.taskId) AS totalTasks,
            COALESCE(SUM(CASE 
                WHEN t.status = com.uniwork.enums.TaskStatus.COMPLETED THEN 1 ELSE 0 END), 0) AS completedTasks,
            COALESCE(SUM(CASE 
                WHEN t.status = com.uniwork.enums.TaskStatus.PENDING THEN 1 ELSE 0 END), 0) AS pendingTasks,
            COALESCE(SUM(CASE 
                WHEN t.status = com.uniwork.enums.TaskStatus.REVIEWING THEN 1 ELSE 0 END), 0) AS reviewingTasks,
            COALESCE(SUM(CASE 
                WHEN t.status = com.uniwork.enums.TaskStatus.CANCELLED THEN 1 ELSE 0 END), 0) AS cancelledTasks,
            COALESCE(SUM(CASE 
                WHEN t.status = com.uniwork.enums.TaskStatus.DOING THEN 1 ELSE 0 END), 0) AS doingTasks
        FROM Task t
        WHERE t.assignedTo = :userId
    """)
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
                t.managedBy     AS managedBy,
                t.title         AS title,
                t.description   AS description,
                t.priority      AS priority,
                t.status        AS status,
                t.dueDate       AS dueDate,
                t.createdDate   AS createdDate,
                t.updatedDate   AS updatedDate,
                t.completed     AS completed,
                t.tags          AS tags,
                t.parentId      AS taskParentId,
                t.stageId       AS stageId,
                u.name          AS assigneeName,
                creator.name    AS createdByName,
                manager.name    AS managedByName,
                s.name          AS stageName,
                p.name          AS projectName,
                (SELECT COUNT(i) FROM Issue i WHERE i.taskId = t.taskId AND i.isDeleted = false) AS issueCount
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            LEFT JOIN User creator ON t.createdBy = creator.userId
            LEFT JOIN User manager ON t.managedBy = manager.userId
            LEFT JOIN Stage s ON t.stageId = s.stageId
            LEFT JOIN Project p ON t.projectId = p.projectId
            WHERE t.projectId = :projectId
            """)
    List<TaskDetailProjection> findByProjectIdWithUser(Long projectId);

    @Query("""
            SELECT
                t.taskId        AS taskId,
                t.projectId     AS projectId,
                t.assignedTo    AS assignedTo,
                t.createdBy     AS createdBy,
                t.managedBy     AS managedBy,
                t.title         AS title,
                t.description   AS description,
                t.priority      AS priority,
                t.status        AS status,
                t.dueDate       AS dueDate,
                t.createdDate   AS createdDate,
                t.updatedDate   AS updatedDate,
                t.completed     AS completed,
                t.tags          AS tags,
                t.parentId      AS taskParentId,
                t.stageId       AS stageId,
                u.name          AS assigneeName,
                creator.name    AS createdByName,
                manager.name    AS managedByName,
                s.name          AS stageName,
                p.name          AS projectName,
                (SELECT COUNT(i) FROM Issue i WHERE i.taskId = t.taskId AND i.isDeleted = false) AS issueCount
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            LEFT JOIN User creator ON t.createdBy = creator.userId
            LEFT JOIN User manager ON t.managedBy = manager.userId
            LEFT JOIN Stage s ON t.stageId = s.stageId
            LEFT JOIN Project p ON t.projectId = p.projectId
            WHERE t.taskId = :taskId
            """)
    TaskDetailProjection findByTaskId(@Param("taskId") Long taskId);

    @Query("""
            SELECT
                t.taskId        AS taskId,
                t.projectId     AS projectId,
                t.assignedTo    AS assignedTo,
                t.createdBy     AS createdBy,
                t.managedBy     AS managedBy,
                t.title         AS title,
                t.description   AS description,
                t.priority      AS priority,
                t.status        AS status,
                t.dueDate       AS dueDate,
                t.createdDate   AS createdDate,
                t.updatedDate   AS updatedDate,
                t.completed     AS completed,
                t.tags          AS tags,
                t.parentId      AS taskParentId,
                t.stageId       AS stageId,
                u.name          AS assigneeName,
                creator.name    AS createdByName,
                manager.name    AS managedByName,
                s.name          AS stageName,
                p.name          AS projectName,
                (SELECT COUNT(i) FROM Issue i WHERE i.taskId = t.taskId AND i.isDeleted = false) AS issueCount
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            LEFT JOIN User creator ON t.createdBy = creator.userId
            LEFT JOIN User manager ON t.managedBy = manager.userId
            LEFT JOIN Stage s ON t.stageId = s.stageId
            LEFT JOIN Project p ON t.projectId = p.projectId
            WHERE t.parentId = :taskParentId
            """)
    List<TaskDetailProjection> findByParentId(@Param("taskParentId") Long taskParentId);

    List<Task> findByParentIdOrderByTaskIdDesc(Long taskId);

    // =====================================================
    // STAGE-RELATED QUERIES
    // =====================================================

    List<Task> findAllByStageId(Long stageId);

    Long countByStageId(Long stageId);

    Long countByStageIdAndStatus(Long stageId, TaskStatus status);

    boolean existsByStageIdAndStatusNot(Long stageId, TaskStatus status);

    @Query("""
            SELECT
                t.taskId        AS taskId,
                t.projectId     AS projectId,
                t.assignedTo    AS assignedTo,
                t.createdBy     AS createdBy,
                t.managedBy     AS managedBy,
                t.title         AS title,
                t.description   AS description,
                t.priority      AS priority,
                t.status        AS status,
                t.dueDate       AS dueDate,
                t.createdDate   AS createdDate,
                t.updatedDate   AS updatedDate,
                t.completed     AS completed,
                t.tags          AS tags,
                t.parentId      AS taskParentId,
                t.stageId       AS stageId,
                u.name          AS assigneeName,
                creator.name    AS createdByName,
                manager.name    AS managedByName,
                s.name          AS stageName,
                p.name          AS projectName,
                (SELECT COUNT(i) FROM Issue i WHERE i.taskId = t.taskId AND i.isDeleted = false) AS issueCount
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            LEFT JOIN User creator ON t.createdBy = creator.userId
            LEFT JOIN User manager ON t.managedBy = manager.userId
            LEFT JOIN Stage s ON t.stageId = s.stageId
            LEFT JOIN Project p ON t.projectId = p.projectId
            WHERE t.stageId = :stageId
            """)
    List<TaskDetailProjection> findByStageIdWithUser(@Param("stageId") Long stageId);

    @Query("""
            SELECT
                t.taskId        AS taskId,
                t.projectId     AS projectId,
                t.assignedTo    AS assignedTo,
                t.createdBy     AS createdBy,
                t.managedBy     AS managedBy,
                t.title         AS title,
                t.description   AS description,
                t.priority      AS priority,
                t.status        AS status,
                t.dueDate       AS dueDate,
                t.createdDate   AS createdDate,
                t.updatedDate   AS updatedDate,
                t.completed     AS completed,
                t.tags          AS tags,
                t.parentId      AS taskParentId,
                t.stageId       AS stageId,
                u.name          AS assigneeName,
                creator.name    AS createdByName,
                manager.name    AS managedByName,
                s.name          AS stageName,
                p.name          AS projectName,
                (SELECT COUNT(i) FROM Issue i WHERE i.taskId = t.taskId AND i.isDeleted = false) AS issueCount
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            LEFT JOIN User creator ON t.createdBy = creator.userId
            LEFT JOIN User manager ON t.managedBy = manager.userId
            LEFT JOIN Stage s ON t.stageId = s.stageId
            LEFT JOIN Project p ON t.projectId = p.projectId
            WHERE t.assignedTo = :assignedTo
              AND (:priority IS NULL OR t.priority = :priority)
              AND (:status IS NULL OR t.status = :status)
            """)
    List<TaskDetailProjection> findByAssignedToWithUser(@Param("assignedTo") Long assignedTo,
                                                         @Param("priority") Priority priority,
                                                         @Param("status") TaskStatus status);

    // =====================================================
    // REPORT QUERIES
    // =====================================================

    @Query("""
            SELECT
                t.taskId AS taskId,
                t.title AS title,
                t.status AS status,
                t.priority AS priority,
                t.dueDate AS dueDate,
                t.assignedTo AS assignedTo,
                u.name AS assigneeName,
                t.projectId AS projectId,
                p.name AS projectName
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            LEFT JOIN Project p ON t.projectId = p.projectId
            WHERE t.assignedTo = :userId
              AND t.dueDate < CURRENT_TIMESTAMP
              AND t.status NOT IN (com.uniwork.enums.TaskStatus.COMPLETED, com.uniwork.enums.TaskStatus.CANCELLED)
            ORDER BY t.dueDate ASC
            """)
    List<ReportOverdueTaskProjection> findOverdueTasks(@Param("userId") Long userId);

    @Query("""
            SELECT
                t.assignedTo AS userId,
                u.name AS userName,
                COUNT(t.taskId) AS totalTasks,
                SUM(CASE WHEN t.status = 3 THEN 1 ELSE 0 END) AS completedTasks,
                SUM(CASE WHEN t.status = 0 THEN 1 ELSE 0 END) AS pendingTasks,
                SUM(CASE WHEN t.status = 1 THEN 1 ELSE 0 END) AS doingTasks
            FROM Task t
            LEFT JOIN User u ON t.assignedTo = u.userId
            WHERE t.projectId IN :projectIds
              AND t.assignedTo IS NOT NULL
            GROUP BY t.assignedTo, u.name
            """)
    List<ReportMemberWorkloadProjection> getMemberWorkload(@Param("projectIds") List<Long> projectIds);

}
