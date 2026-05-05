package com.uniwork.modules.issue.service;

import com.uniwork.modules.issue.dto.IssueDTO;
import com.uniwork.modules.issue.entity.Issue;
import com.uniwork.modules.issue.request.IssueRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IssueService {

    List<IssueDTO> getIssuesByTaskId(Long taskId);

    IssueDTO getIssueById(Long issueId);

    Issue createIssue(Long userId, IssueRequest request);

    Issue updateIssue(Long userId, Long issueId, IssueRequest request);

    Issue deleteIssue(Long userId, Long issueId);

    List<IssueDTO> getIssuesByAssignedTo(Long userId);

    Page<IssueDTO> getIssuesByProjectId(Long projectId, Pageable pageable);
}
