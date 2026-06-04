package com.uniwork.modules.issue.repository;

import com.uniwork.enums.IssueStatus;
import com.uniwork.modules.issue.entity.Issue;
import com.uniwork.modules.issue.projection.IssueDetailProjection;
import com.uniwork.modules.issue.projection.ReportIssueProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findAllByTaskId(Long taskId);

    List<Issue> findAllByProjectId(Long projectId);

    Long countByTaskId(Long taskId);

    Long countByTaskIdAndStatus(Long taskId, IssueStatus status);

    Long countByProjectId(Long projectId);

    Long countByAssignedTo(Long userId);

    @Query("""
            SELECT
                i.issueId       AS issueId,
                i.taskId        AS taskId,
                i.projectId     AS projectId,
                i.reportedBy    AS reportedBy,
                i.assignedTo    AS assignedTo,
                i.title         AS title,
                i.description   AS description,
                i.type          AS type,
                i.priority      AS priority,
                i.status        AS status,
                i.dueDate       AS dueDate,
                i.createdDate   AS createdDate,
                i.updatedDate   AS updatedDate,
                reporter.name   AS reporterName,
                assignee.name   AS assigneeName,
                t.title         AS taskTitle
            FROM Issue i
            LEFT JOIN User reporter ON i.reportedBy = reporter.userId
            LEFT JOIN User assignee ON i.assignedTo = assignee.userId
            LEFT JOIN Task t ON i.taskId = t.taskId
            WHERE i.taskId = :taskId
            """)
    List<IssueDetailProjection> findByTaskIdWithDetails(@Param("taskId") Long taskId);

    @Query("""
            SELECT
                i.issueId       AS issueId,
                i.taskId        AS taskId,
                i.projectId     AS projectId,
                i.reportedBy    AS reportedBy,
                i.assignedTo    AS assignedTo,
                i.title         AS title,
                i.description   AS description,
                i.type          AS type,
                i.priority      AS priority,
                i.status        AS status,
                i.dueDate       AS dueDate,
                i.createdDate   AS createdDate,
                i.updatedDate   AS updatedDate,
                reporter.name   AS reporterName,
                assignee.name   AS assigneeName,
                t.title         AS taskTitle
            FROM Issue i
            LEFT JOIN User reporter ON i.reportedBy = reporter.userId
            LEFT JOIN User assignee ON i.assignedTo = assignee.userId
            LEFT JOIN Task t ON i.taskId = t.taskId
            WHERE i.issueId = :issueId
            """)
    IssueDetailProjection findByIssueIdWithDetails(@Param("issueId") Long issueId);

    @Query("""
            SELECT
                i.issueId       AS issueId,
                i.taskId        AS taskId,
                i.projectId     AS projectId,
                i.reportedBy    AS reportedBy,
                i.assignedTo    AS assignedTo,
                i.title         AS title,
                i.description   AS description,
                i.type          AS type,
                i.priority      AS priority,
                i.status        AS status,
                i.dueDate       AS dueDate,
                i.createdDate   AS createdDate,
                i.updatedDate   AS updatedDate,
                reporter.name   AS reporterName,
                assignee.name   AS assigneeName,
                t.title         AS taskTitle
            FROM Issue i
            LEFT JOIN User reporter ON i.reportedBy = reporter.userId
            LEFT JOIN User assignee ON i.assignedTo = assignee.userId
            LEFT JOIN Task t ON i.taskId = t.taskId
            WHERE i.assignedTo = :userId
            AND i.status IN :statuses
            """)
    Page<IssueDetailProjection> findByAssignedToAndStatusInWithDetails(
            @Param("userId") Long userId,
            @Param("statuses") List<IssueStatus> statuses,
            Pageable pageable);

    @Query("""
            SELECT
                COUNT(i) AS totalIssues,
                SUM(CASE WHEN i.status = 2 OR i.status = 3 THEN 1 ELSE 0 END) AS completedIssues,
                SUM(CASE WHEN i.status = 0 OR i.status = 4 THEN 1 ELSE 0 END) AS pendingIssues,
                SUM(CASE WHEN i.status = 1 THEN 1 ELSE 0 END) AS doingIssues
            FROM Issue i
            WHERE i.assignedTo = :userId
            AND i.isDeleted = false
            """)
    ReportIssueProjection getIssueReport(@Param("userId") Long userId);

    @Query("""
            SELECT
                i.issueId       AS issueId,
                i.taskId        AS taskId,
                i.projectId     AS projectId,
                i.reportedBy    AS reportedBy,
                i.assignedTo    AS assignedTo,
                i.title         AS title,
                i.description   AS description,
                i.type          AS type,
                i.priority      AS priority,
                i.status        AS status,
                i.dueDate       AS dueDate,
                i.createdDate   AS createdDate,
                i.updatedDate   AS updatedDate,
                reporter.name   AS reporterName,
                assignee.name   AS assigneeName,
                t.title         AS taskTitle
            FROM Issue i
            LEFT JOIN User reporter ON i.reportedBy = reporter.userId
            LEFT JOIN User assignee ON i.assignedTo = assignee.userId
            LEFT JOIN Task t ON i.taskId = t.taskId
            WHERE i.projectId = :projectId
            """)
    Page<IssueDetailProjection> findByProjectIdWithDetails(@Param("projectId") Long projectId, Pageable pageable);

    @Query("""
            SELECT
                i.issueId AS issueId,
                i.title AS title,
                i.status AS status,
                i.priority AS priority,
                i.dueDate AS dueDate,
                i.assignedTo AS assignedTo,
                assignee.name AS assigneeName,
                i.projectId AS projectId,
                p.name AS projectName
            FROM Issue i
            LEFT JOIN User assignee ON i.assignedTo = assignee.userId
            LEFT JOIN Project p ON i.projectId = p.projectId
            WHERE i.assignedTo = :userId
              AND i.dueDate < CURRENT_TIMESTAMP
              AND i.status NOT IN (2, 3)
            ORDER BY i.dueDate ASC
            """)
    List<com.uniwork.modules.report.projection.ReportOverdueIssueProjection> findOverdueIssues(@Param("userId") Long userId);

    @Query("""
            SELECT
                i.assignedTo AS userId,
                u.name AS userName,
                COUNT(i.issueId) AS totalIssues,
                SUM(CASE WHEN i.status = 2 OR i.status = 3 THEN 1 ELSE 0 END) AS completedIssues,
                SUM(CASE WHEN i.status = 0 OR i.status = 4 THEN 1 ELSE 0 END) AS pendingIssues
            FROM Issue i
            LEFT JOIN User u ON i.assignedTo = u.userId
            WHERE i.projectId IN :projectIds
              AND i.assignedTo IS NOT NULL
              AND i.isDeleted = false
            GROUP BY i.assignedTo, u.name
            """)
    List<com.uniwork.modules.report.projection.ReportMemberIssueWorkloadProjection> getMemberIssueWorkload(@Param("projectIds") List<Long> projectIds);

    @Query("""
            SELECT
                i.issueId       AS issueId,
                i.taskId        AS taskId,
                i.projectId     AS projectId,
                i.reportedBy    AS reportedBy,
                i.assignedTo    AS assignedTo,
                i.title         AS title,
                i.description   AS description,
                i.type          AS type,
                i.priority      AS priority,
                i.status        AS status,
                i.dueDate       AS dueDate,
                i.createdDate   AS createdDate,
                i.updatedDate   AS updatedDate,
                reporter.name   AS reporterName,
                assignee.name   AS assigneeName,
                t.title         AS taskTitle
            FROM Issue i
            LEFT JOIN User reporter ON i.reportedBy = reporter.userId
            LEFT JOIN User assignee ON i.assignedTo = assignee.userId
            LEFT JOIN Task t ON i.taskId = t.taskId
            WHERE i.assignedTo = :userId
            """)
    List<IssueDetailProjection> findByAssignedToWithDetails(@Param("userId") Long userId);

    Long countByProjectIdAndStatus(Long projectId, IssueStatus status);

    @Query("""
            SELECT COUNT(i)
            FROM Issue i
            WHERE i.taskId IN (SELECT t.taskId FROM Task t WHERE t.stageId = :stageId)
              AND i.isDeleted = false
            """)
    Long countByStageId(@Param("stageId") Long stageId);

    @Query("""
            SELECT COUNT(i)
            FROM Issue i
            WHERE i.taskId IN (SELECT t.taskId FROM Task t WHERE t.stageId = :stageId)
              AND i.isDeleted = false
              AND (i.status = 2 OR i.status = 3)
            """)
    Long countResolvedByStageId(@Param("stageId") Long stageId);

    @Query("""
            SELECT COUNT(i)
            FROM Issue i
            WHERE i.taskId IN (SELECT t.taskId FROM Task t WHERE t.stageId = :stageId)
              AND i.isDeleted = false
              AND i.status = 0
            """)
    Long countOpenByStageId(@Param("stageId") Long stageId);
}

