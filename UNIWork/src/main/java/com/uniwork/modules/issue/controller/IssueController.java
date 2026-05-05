package com.uniwork.modules.issue.controller;

import com.uniwork.common.response.PageMetadata;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.issue.dto.IssueDTO;
import com.uniwork.modules.issue.entity.Issue;
import com.uniwork.modules.issue.request.IssueRequest;
import com.uniwork.modules.issue.service.IssueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/issues")
@PreAuthorize("hasAuthority('PERM_MANAGE_TASKS')")
public class IssueController {

    @Autowired
    private IssueService issueService;

    // =====================================================
    // GET ISSUES BY TASK
    // =====================================================

    @GetMapping("/task/{taskId}")
    public ResponseEntity getIssuesByTask(@PathVariable Long taskId,
                                           @RequestAttribute(required = false) Payload payload) {
        log.info("Fetching issues for task ID: {}", taskId);
        List<IssueDTO> issues = issueService.getIssuesByTaskId(taskId);
        return ResponseFactory.success(issues);
    }

    // =====================================================
    // GET ISSUES BY PROJECT (paginated)
    // =====================================================

    @GetMapping("/project/{projectId}")
    public ResponseEntity getIssuesByProject(@PathVariable Long projectId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestAttribute(required = false) Payload payload) {
        log.info("Fetching issues for project ID: {}", projectId);
        Pageable pageable = PageRequest.of(page, size);
        Page<IssueDTO> issuePage = issueService.getIssuesByProjectId(projectId, pageable);

        PageMetadata metadata = PageMetadata.of(
                issuePage.getNumber(),
                issuePage.getSize(),
                issuePage.getTotalElements()
        );

        return ResponseFactory.makePagination(issuePage.getContent(), metadata);
    }

    // =====================================================
    // GET MY ISSUES (assigned to current user)
    // =====================================================

    @GetMapping("/my-issues")
    public ResponseEntity getMyIssues(@RequestAttribute(required = false) Payload payload) {
        log.info("Fetching issues assigned to user: {}", payload.getUserId());
        List<IssueDTO> issues = issueService.getIssuesByAssignedTo(payload.getUserId());
        return ResponseFactory.success(issues);
    }

    // =====================================================
    // GET ISSUE DETAIL
    // =====================================================

    @GetMapping("/{issueId}")
    public ResponseEntity getIssueDetail(@PathVariable Long issueId,
                                          @RequestAttribute(required = false) Payload payload) {
        log.info("Fetching issue detail for issue ID: {}", issueId);
        IssueDTO issue = issueService.getIssueById(issueId);
        return ResponseFactory.success(issue);
    }

    // =====================================================
    // CREATE ISSUE
    // =====================================================

    @PostMapping
    public ResponseEntity createIssue(@RequestBody IssueRequest issueRequest,
                                       @RequestAttribute(required = false) Payload payload) {
        log.info("Creating issue for task ID: {}", issueRequest.getTaskId());
        Issue issue = issueService.createIssue(payload.getUserId(), issueRequest);
        return ResponseFactory.success(issue);
    }

    // =====================================================
    // UPDATE ISSUE
    // =====================================================

    @PutMapping("/{issueId}")
    public ResponseEntity updateIssue(@PathVariable Long issueId,
                                       @RequestBody IssueRequest issueRequest,
                                       @RequestAttribute(required = false) Payload payload) {
        log.info("Updating issue ID: {}", issueId);
        Issue issue = issueService.updateIssue(payload.getUserId(), issueId, issueRequest);
        return ResponseFactory.success(issue);
    }

    // =====================================================
    // DELETE ISSUE (soft delete)
    // =====================================================

    @DeleteMapping("/{issueId}")
    public ResponseEntity deleteIssue(@PathVariable Long issueId,
                                       @RequestAttribute(required = false) Payload payload) {
        log.info("Deleting issue ID: {}", issueId);
        Issue issue = issueService.deleteIssue(payload.getUserId(), issueId);
        return ResponseFactory.success(issue);
    }
}
