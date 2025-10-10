package com.uniwork.service;

import com.uniwork.entity.dto.ProjectReportDTO;
import com.uniwork.entity.dto.TaskReportDTO;
import com.uniwork.entity.model.Project;
import com.uniwork.entity.model.Task;
import com.uniwork.repository.ProjectMemberRepository;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    public ResponseEntity<List<ProjectReportDTO>> getProjectReport(Long userId, Long begin, Long end) {
        List<Long> projectIds = projectMemberRepository.findProjectIdsByUserId(userId);

        List<CompletableFuture<ProjectReportDTO>> futures = projectIds.stream()
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
                        Double completedPercent = total == 0 ? 0.0 : (completed * 100) / total;
                        return new ProjectReportDTO(project, total, completed, pending, doing, completedPercent);
                    } catch (Exception e) {
                        throw new RuntimeException("Error while generating report for projectId=" + projectId, e);
                    }
                }))
                .toList();

        List<ProjectReportDTO> reports = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        return ResponseEntity.ok(reports);
    }

    public ResponseEntity<TaskReportDTO> getTaskReport(Long userId, Long begin, Long end) {

        CompletableFuture<Long> totalTask =
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedTo(userId));
        CompletableFuture<Long> completedTask =
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedToAndStatusEqualsIgnoreCase(userId, "COMPLETED"));
        CompletableFuture<Long> pendingTask =
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedToAndStatusEqualsIgnoreCase(userId, "PENDING"));
        CompletableFuture<Long> doingTask =
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedToAndStatusEqualsIgnoreCase(userId, "DOING"));
        CompletableFuture.allOf(totalTask, completedTask, pendingTask, doingTask).join();

        try {
            Long total = totalTask.get();
            Long completed = completedTask.get();
            Long pending = pendingTask.get();
            Long doing = doingTask.get();
            Double completedPercent = total == 0 ? 0.0 : (completed * 100) / total;
            TaskReportDTO taskReportDTO = new TaskReportDTO(total, completed, pending, doing, completedPercent);
            return ResponseEntity.ok(taskReportDTO);
        } catch (Exception e) {
            throw new RuntimeException("Error while generating task report for userId=" + userId, e);
        }
    }
}
