package com.uniwork.service;

import com.uniwork.model.dto.ProjectReportDTO;
import com.uniwork.model.dto.TaskPerformanceDTO;
import com.uniwork.model.dto.TaskReportDTO;
import com.uniwork.model.entity.Event;
import com.uniwork.model.entity.Task;
import com.uniwork.model.response.StatsResponse;
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
