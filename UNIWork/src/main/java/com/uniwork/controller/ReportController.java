package com.uniwork.controller;

import com.uniwork.entity.dto.ProjectReportDTO;
import com.uniwork.entity.dto.TaskPerformanceDTO;
import com.uniwork.entity.dto.TaskReportDTO;
import com.uniwork.entity.model.Event;
import com.uniwork.entity.model.Task;
import com.uniwork.entity.response.ResponseFactory;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/project-report")
    public ResponseEntity getProjectReport(@RequestAttribute(required = false) Payload payload,
                                           @RequestParam(required = false) Long begin,
                                           @RequestParam(required = false) Long end) {
        List<ProjectReportDTO> projectReportDTOS = reportService.getProjectReport(payload.getUserId(), begin, end);
        return ResponseFactory.success(projectReportDTOS);
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
                                          @RequestParam(required = false) Long end) {
        List<Task> tasks = reportService.getPendingTask(payload.getUserId(), begin, end);
        return ResponseFactory.success(tasks);
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

}
