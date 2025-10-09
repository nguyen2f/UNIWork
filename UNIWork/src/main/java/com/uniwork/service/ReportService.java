package com.uniwork.service;

import com.uniwork.entity.dto.ReportDTO;
import com.uniwork.entity.model.Project;
import com.uniwork.repository.ProjectMemberRepository;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class ReportService {

    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;

    public ReportService(TaskRepository taskRepository, ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
    }

    public ResponseEntity<List<ReportDTO>> getProjectReport(Long userId, Long begin, Long end) {
        List<Long> projectIds = projectMemberRepository.findProjectIdsByUserId(userId);

        List<CompletableFuture<ReportDTO>> futures = projectIds.stream()
                .map(projectId -> CompletableFuture.supplyAsync(() -> {
                    Project project = projectRepository.findProjectByProjectId(projectId);

                    CompletableFuture<Long> totalTask =
                            CompletableFuture.supplyAsync(() -> taskRepository.countAllByProjectId(projectId));
                    CompletableFuture<Long> completedTask =
                            CompletableFuture.supplyAsync(() -> taskRepository.countAllByProjectIdAndStatusEqualsIgnoreCase(projectId, "COMPLETED"));
                    CompletableFuture<Long> pendingTask =
                            CompletableFuture.supplyAsync(() -> taskRepository.countAllByProjectIdAndStatusEqualsIgnoreCase(projectId, "PENDING"));
                    CompletableFuture<Long> doingTask =
                            CompletableFuture.supplyAsync(() -> taskRepository.countAllByProjectIdAndStatusEqualsIgnoreCase(projectId, "DOING"));

                    CompletableFuture.allOf(totalTask, completedTask, pendingTask, doingTask).join();

                    try {
                        Long total = totalTask.get();
                        Long completed = completedTask.get();
                        Long pending = pendingTask.get();
                        Long doing = doingTask.get();
                        return new ReportDTO(project, total, completed, pending, doing);
                    } catch (Exception e) {
                        throw new RuntimeException("Error while generating report for projectId=" + projectId, e);
                    }
                }))
                .toList();

        List<ReportDTO> reports = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        return ResponseEntity.ok(reports);
    }

}
