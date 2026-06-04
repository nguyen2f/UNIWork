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

}
