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
    public ResponseEntity getProjectReport(@RequestAttribute Payload payload, @RequestParam Long begin, @RequestParam Long end) {
        return reportService.getProjectReport(payload.getUserId(), begin, end);
    }
}
