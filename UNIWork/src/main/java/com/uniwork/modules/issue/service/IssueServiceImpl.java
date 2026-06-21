package com.uniwork.modules.issue.service;

import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.enums.IssueStatus;
import com.uniwork.enums.IssueType;
import com.uniwork.enums.Priority;
import com.uniwork.modules.issue.dto.IssueDTO;
import com.uniwork.modules.issue.entity.Issue;
import com.uniwork.modules.issue.projection.IssueDetailProjection;
import com.uniwork.modules.issue.repository.IssueRepository;
import com.uniwork.modules.issue.request.IssueRequest;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.task.repository.TaskRepository;
import com.uniwork.common.utils.BeanCopyUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<IssueDTO> getIssuesByTaskId(Long taskId) {
        List<IssueDetailProjection> projections = issueRepository.findByTaskIdWithDetails(taskId);
        return projections.stream().map(IssueDTO::new).toList();
    }

    @Override
    public IssueDTO getIssueById(Long issueId) {
        IssueDetailProjection projection = issueRepository.findByIssueIdWithDetails(issueId);
        if (projection == null) {
            throw new CoreException(ErrorCode.ISSUE_NOT_FOUND, "Issue not found");
        }
        return new IssueDTO(projection);
    }

    @Override
    @Transactional
    public Issue createIssue(Long userId, IssueRequest request) {
        // Validate that parent task exists
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new CoreException(ErrorCode.TASK_NOT_FOUND, "Parent task not found"));

        Issue issue = new Issue();
        issue.setTaskId(task.getTaskId());
        issue.setProjectId(task.getProjectId());
        issue.setStageId(task.getStageId());
        issue.setReportedBy(userId);
        issue.setAssignedTo(request.getAssignedTo());
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());

        if (request.getType() != null) {
            issue.setType(IssueType.fromCode(request.getType()));
        } else {
            issue.setType(IssueType.OTHER);
        }

        if (request.getPriority() != null) {
            issue.setPriority(Priority.fromCode(request.getPriority()));
        } else {
            issue.setPriority(Priority.MEDIUM);
        }

        issue.setStatus(IssueStatus.OPEN);
        issue.setDueDate(request.getDueDate());
        issue.setCreatedDate(LocalDateTime.now().withNano(0));

        return issueRepository.save(issue);
    }

    @Override
    @Transactional
    public Issue updateIssue(Long userId, Long issueId, IssueRequest request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new CoreException(ErrorCode.ISSUE_NOT_FOUND, "Issue not found"));

        issue.setUpdatedDate(LocalDateTime.now());
        issue.setUpdatedBy(userId);

        if (request.getStatus() != null) {
            issue.setStatus(IssueStatus.fromCode(request.getStatus()));
        }

        if (request.getPriority() != null) {
            issue.setPriority(Priority.fromCode(request.getPriority()));
        }

        if (request.getType() != null) {
            issue.setType(IssueType.fromCode(request.getType()));
        }

        BeanCopyUtils.copyNonNullProperties(request, issue,
                "issueId", "taskId", "projectId", "reportedBy",
                "createdDate", "isDeleted", "status", "priority", "type");

        return issueRepository.save(issue);
    }

    @Override
    @Transactional
    public Issue deleteIssue(Long userId, Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new CoreException(ErrorCode.ISSUE_NOT_FOUND, "Issue not found"));

        // Soft delete
        issue.setIsDeleted(true);
        issue.setUpdatedDate(LocalDateTime.now());
        issue.setUpdatedBy(userId);

        return issueRepository.save(issue);
    }

    @Override
    public List<IssueDTO> getIssuesByAssignedTo(Long userId) {
        List<IssueDetailProjection> projections = issueRepository.findByAssignedToWithDetails(userId);
        return projections.stream().map(IssueDTO::new).toList();
    }

    @Override
    public Page<IssueDTO> getIssuesByProjectId(Long projectId, Pageable pageable) {
        Page<IssueDetailProjection> projections = issueRepository.findByProjectIdWithDetails(projectId, pageable);
        return projections.map(IssueDTO::new);
    }
}
