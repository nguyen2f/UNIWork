package com.uniwork.modules.report.service;

import com.uniwork.modules.report.dto.*;
import com.uniwork.modules.task.dto.TaskPerformanceDTO;
import com.uniwork.modules.event.entity.Event;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.report.response.StatsResponse;
import com.uniwork.modules.issue.projection.IssueDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {

    Page<ProjectReportDTO> getProjectReportV2(Long userId, Long begin, Long end, Pageable pageable);

    TaskReportDTO getTaskReport(Long userId, Long begin, Long end);

    Page<Task> getPendingTask(Long userId, Long begin, Long end, Pageable pageable);

    Page<IssueDetailProjection> getPendingIssues(Long userId, Long begin, Long end, Pageable pageable);

    TaskPerformanceDTO getTasksPerformance(Long userId, Long begin, Long end);

    List<Event> getUpcomingEvents(Long userId, Long begin, Long end);

    List<StatsResponse> getStats(Long userId);

    // New report APIs
    SingleProjectReportDTO getProjectDetailReport(Long userId, Long projectId);

    List<MemberWorkloadDTO> getMemberWorkload(Long userId);

    List<OverdueItemDTO> getOverdueItems(Long userId);

    List<StageReportDTO> getStageReport(Long userId, Long projectId);
}
