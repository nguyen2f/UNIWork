package com.uniwork.modules.report.service;

import com.uniwork.modules.report.dto.ProjectReportDTO;
import com.uniwork.modules.task.dto.TaskPerformanceDTO;
import com.uniwork.modules.report.dto.TaskReportDTO;
import com.uniwork.modules.event.entity.Event;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.report.response.StatsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {

    Page<ProjectReportDTO> getProjectReportV2(Long userId, Long begin, Long end, Pageable pageable);

    TaskReportDTO getTaskReport(Long userId, Long begin, Long end);

    Page<Task> getPendingTask(Long userId, Long begin, Long end, Pageable pageable);

    TaskPerformanceDTO getTasksPerformance(Long userId, Long begin, Long end);

    List<Event> getUpcomingEvents(Long userId, Long begin, Long end);

    List<StatsResponse> getStats(Long userId);
}
