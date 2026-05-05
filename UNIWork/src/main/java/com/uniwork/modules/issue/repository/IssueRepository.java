package com.uniwork.modules.issue.repository;

import com.uniwork.enums.IssueStatus;
import com.uniwork.modules.issue.entity.Issue;
import com.uniwork.modules.issue.projection.IssueDetailProjection;
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
            """)
    List<IssueDetailProjection> findByAssignedToWithDetails(@Param("userId") Long userId);

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
}
