package com.uniwork.controller;

import com.uniwork.interceptors.Payload;
import com.uniwork.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/project-report")
    public ResponseEntity getProjectReport(@RequestAttribute Payload payload,
                                           @RequestParam(required = false) Long begin,
                                           @RequestParam(required = false) Long end) {
        return reportService.getProjectReport(payload.getUserId(), begin, end);
    }

    @GetMapping("/task-report")
    public ResponseEntity getTaskReport(@RequestAttribute Payload payload,
                                        @RequestParam(required = false) Long begin,
                                        @RequestParam(required = false) Long end) {
        return reportService.getTaskReport(payload.getUserId(), begin, end);
    }

    @GetMapping("/task-report/pending-tasks")
    public ResponseEntity getPendingTasks(@RequestAttribute Payload payload,
                                         @RequestParam(required = false) Long begin,
                                         @RequestParam(required = false) Long end) {
        return reportService.getPendingTask(payload.getUserId(), begin, end);
    }

    @GetMapping("/task-report/tasks-performance")
    public ResponseEntity getTasksPerformance(@RequestAttribute Payload payload,
                                         @RequestParam(required = false) Long begin,
                                         @RequestParam(required = false) Long end) {
        return reportService.getTasksPerformance(payload.getUserId(), begin, end);
    }

    @GetMapping("/event-report/upcoming-events")
    public ResponseEntity getUpcomingEvents(@RequestAttribute Payload payload,
                                             @RequestParam(required = false) Long begin,
                                             @RequestParam(required = false) Long end) {
        return reportService.getUpcomingEvents(payload.getUserId(), begin, end);
    }

}
