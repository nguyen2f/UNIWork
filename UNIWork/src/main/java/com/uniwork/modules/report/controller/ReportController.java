package com.uniwork.modules.report.controller;

import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.issue.projection.IssueDetailProjection;
import com.uniwork.modules.report.dto.*;
import com.uniwork.modules.task.dto.TaskPerformanceDTO;
import com.uniwork.modules.event.entity.Event;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.enums.Priority;
import com.uniwork.enums.ProjectStatus;
import com.uniwork.common.response.PageMetadata;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.report.response.StatsResponse;
import com.uniwork.modules.report.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@PreAuthorize("hasAuthority('PERM_MANAGE_REPORTS')")
@RestController
@RequestMapping("/reports")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/project-report")
    public ResponseEntity getProjectReport(@RequestAttribute(required = false) Payload payload,
                                           @RequestParam(required = false) Long begin,
                                           @RequestParam(required = false) Long end,
                                           @RequestParam(value = "priority", required = false) Priority priority,
                                           @RequestParam(value = "status", required = false) ProjectStatus projectStatus,
                                           Pageable pageable) {
        Page<ProjectReportDTO> page = reportService.getProjectReportV2(payload.getUserId(), begin, end, pageable);

        PageMetadata metadata = PageMetadata.of(page.getNumber(), page.getSize(), page.getTotalElements());

        return ResponseFactory.makePagination(page.getContent(), metadata);
    }

    @GetMapping("/v2/project-report")
    public ResponseEntity<?> getProjectReport(
            @RequestAttribute Payload payload,
            @RequestParam(required = false) Long begin,
            @RequestParam(required = false) Long end,
            Pageable pageable
    ) {
        Page<ProjectReportDTO> page = reportService.getProjectReportV2(payload.getUserId(), begin, end, pageable);

        PageMetadata metadata = PageMetadata.of(page.getNumber(), page.getSize(), page.getTotalElements());

        return ResponseFactory.makePagination(page.getContent(), metadata);
    }


    @GetMapping("/task-report")
    public ResponseEntity getTaskReport(@RequestAttribute(required = false) Payload payload,
                                        @RequestParam(required = false) Long begin,
                                        @RequestParam(required = false) Long end) {
        TaskReportDTO taskReportDTO = reportService.getTaskReport(payload.getUserId(), begin, end);
        return ResponseFactory.success(taskReportDTO);
    }

    @GetMapping("/task-report/pending-tasks")
    public ResponseEntity getPendingTasks(@RequestAttribute(required = false) Payload payload,
                                          @RequestParam(required = false) Long begin,
                                          @RequestParam(required = false) Long end,
                                          Pageable pageable) {
        Page<Task> page = reportService.getPendingTask(payload.getUserId(), begin, end, pageable);
        PageMetadata metadata = PageMetadata.of(page.getNumber(), page.getSize(), page.getTotalElements());
        return ResponseFactory.makePagination(page.getContent(), metadata);
    }

    @GetMapping("/task-report/pending-issues")
    public ResponseEntity getPendingIssues(@RequestAttribute(required = false) Payload payload,
                                          @RequestParam(required = false) Long begin,
                                          @RequestParam(required = false) Long end,
                                          Pageable pageable) {
        Page<IssueDetailProjection> page = reportService.getPendingIssues(payload.getUserId(), begin, end, pageable);
        PageMetadata metadata = PageMetadata.of(page.getNumber(), page.getSize(), page.getTotalElements());
        return ResponseFactory.makePagination(page.getContent(), metadata);
    }

    @GetMapping("/task-report/tasks-performance")
    public ResponseEntity getTasksPerformance(@RequestAttribute(required = false) Payload payload,
                                              @RequestParam(required = false) Long begin,
                                              @RequestParam(required = false) Long end) {
        TaskPerformanceDTO taskPerformanceDTO = reportService.getTasksPerformance(payload.getUserId(), begin, end);
        return ResponseFactory.success(taskPerformanceDTO);
    }

    @GetMapping("/event-report/upcoming-events")
    public ResponseEntity getUpcomingEvents(@RequestAttribute(required = false) Payload payload,
                                            @RequestParam(required = false) Long begin,
                                            @RequestParam(required = false) Long end) {
        List<Event> events = reportService.getUpcomingEvents(payload.getUserId(), begin, end);
        return ResponseFactory.success(events);
    }

    @GetMapping("/stats")
    public ResponseEntity getStats(@RequestAttribute Payload payload) {
        List<StatsResponse> statsResponses = reportService.getStats(payload.getUserId());
        return ResponseFactory.success(statsResponses);
    }

    // =====================================================
    // NEW REPORT ENDPOINTS
    // =====================================================

    @GetMapping("/project-report/{projectId}")
    public ResponseEntity getProjectDetailReport(@PathVariable Long projectId,
                                                  @RequestAttribute Payload payload) {
        log.info("Getting detailed report for project ID: {}", projectId);
        SingleProjectReportDTO report = reportService.getProjectDetailReport(payload.getUserId(), projectId);
        return ResponseFactory.success(report);
    }

    @GetMapping("/member-workload")
    public ResponseEntity getMemberWorkload(@RequestAttribute Payload payload) {
        log.info("Getting member workload report for user ID: {}", payload.getUserId());
        List<MemberWorkloadDTO> workload = reportService.getMemberWorkload(payload.getUserId());
        return ResponseFactory.success(workload);
    }

    @GetMapping("/overdue-items")
    public ResponseEntity getOverdueItems(@RequestAttribute Payload payload) {
        log.info("Getting overdue items for user ID: {}", payload.getUserId());
        List<OverdueItemDTO> overdueItems = reportService.getOverdueItems(payload.getUserId());
        return ResponseFactory.success(overdueItems);
    }

    @GetMapping("/stage-report/{projectId}")
    public ResponseEntity getStageReport(@PathVariable Long projectId,
                                          @RequestAttribute Payload payload) {
        log.info("Getting stage report for project ID: {}", projectId);
        List<StageReportDTO> stageReport = reportService.getStageReport(payload.getUserId(), projectId);
        return ResponseFactory.success(stageReport);
    }

    // =====================================================
    // ANALYTICS ENDPOINTS
    // =====================================================

    /**
     * Get completion trend of tasks/issues over time.
     * Granularity: DAILY, MONTHLY, YEARLY (default: DAILY)
     * from/to: epoch millis (default: last 30 days for DAILY, last 12 months for MONTHLY, last 5 years for YEARLY)
     */
    @GetMapping("/analytics/completion-trend")
    public ResponseEntity<?> getCompletionTrend(
            @RequestAttribute Payload payload,
            @RequestParam(defaultValue = "DAILY") String granularity,
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Long to) {

        LocalDateTime[] range = resolveTimeRange(granularity, from, to);
        CompletionTrendDTO trend = reportService.getCompletionTrend(
                payload.getUserId(), granularity, range[0], range[1]);
        return ResponseFactory.success(trend);
    }

    /**
     * Get creation trend of tasks/issues over time.
     * Granularity: DAILY, MONTHLY, YEARLY (default: DAILY)
     */
    @GetMapping("/analytics/creation-trend")
    public ResponseEntity<?> getCreationTrend(
            @RequestAttribute Payload payload,
            @RequestParam(defaultValue = "DAILY") String granularity,
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Long to) {

        LocalDateTime[] range = resolveTimeRange(granularity, from, to);
        CreationTrendDTO trend = reportService.getCreationTrend(
                payload.getUserId(), granularity, range[0], range[1]);
        return ResponseFactory.success(trend);
    }

    /**
     * Get comprehensive analytics summary (totals, rates, distributions).
     */
    @GetMapping("/analytics/summary")
    public ResponseEntity<?> getAnalyticsSummary(@RequestAttribute Payload payload) {
        log.info("Getting analytics summary for user ID: {}", payload.getUserId());
        AnalyticsSummaryDTO summary = reportService.getAnalyticsSummary(payload.getUserId());
        return ResponseFactory.success(summary);
    }

    /**
     * Get detailed analytics for a specific project.
     */
    @GetMapping("/analytics/project/{projectId}")
    public ResponseEntity<?> getProjectAnalytics(
            @PathVariable Long projectId,
            @RequestAttribute Payload payload) {
        log.info("Getting project analytics for project ID: {}", projectId);
        ProjectAnalyticsDTO analytics = reportService.getProjectAnalytics(payload.getUserId(), projectId);
        return ResponseFactory.success(analytics);
    }

    /**
     * Get task status distribution (optionally scoped to a project).
     */
    @GetMapping("/analytics/task-status-distribution")
    public ResponseEntity<?> getTaskStatusDistribution(
            @RequestAttribute Payload payload,
            @RequestParam(required = false) Long projectId) {
        List<StatusDistributionDTO> dist = reportService.getTaskStatusDistribution(payload.getUserId(), projectId);
        return ResponseFactory.success(dist);
    }

    /**
     * Get issue status distribution (optionally scoped to a project).
     */
    @GetMapping("/analytics/issue-status-distribution")
    public ResponseEntity<?> getIssueStatusDistribution(
            @RequestAttribute Payload payload,
            @RequestParam(required = false) Long projectId) {
        List<StatusDistributionDTO> dist = reportService.getIssueStatusDistribution(payload.getUserId(), projectId);
        return ResponseFactory.success(dist);
    }

    /**
     * Get task priority distribution across all user's projects.
     */
    @GetMapping("/analytics/task-priority-distribution")
    public ResponseEntity<?> getTaskPriorityDistribution(@RequestAttribute Payload payload) {
        List<PriorityDistributionDTO> dist = reportService.getTaskPriorityDistribution(payload.getUserId());
        return ResponseFactory.success(dist);
    }

    /**
     * Get issue priority distribution across all user's projects.
     */
    @GetMapping("/analytics/issue-priority-distribution")
    public ResponseEntity<?> getIssuePriorityDistribution(@RequestAttribute Payload payload) {
        List<PriorityDistributionDTO> dist = reportService.getIssuePriorityDistribution(payload.getUserId());
        return ResponseFactory.success(dist);
    }

    // =====================================================
    // PRIVATE HELPERS
    // =====================================================

    private LocalDateTime[] resolveTimeRange(String granularity, Long fromEpoch, Long toEpoch) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate;
        LocalDateTime toDate;

        if (toEpoch != null) {
            toDate = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(toEpoch), java.time.ZoneId.systemDefault());
        } else {
            toDate = now;
        }

        if (fromEpoch != null) {
            fromDate = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(fromEpoch), java.time.ZoneId.systemDefault());
        } else {
            // Default range based on granularity
            fromDate = switch (granularity.toUpperCase()) {
                case "MONTHLY" -> now.minusMonths(12);
                case "YEARLY" -> now.minusYears(5);
                default -> now.minusDays(30); // DAILY
            };
        }

        return new LocalDateTime[]{fromDate, toDate};
    }

}
