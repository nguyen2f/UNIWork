package com.uniwork.service;

import com.uniwork.entity.dto.ProjectReportDTO;
import com.uniwork.entity.dto.TaskPerformanceDTO;
import com.uniwork.entity.dto.TaskReportDTO;
import com.uniwork.entity.enumuration.TaskStatus;
import com.uniwork.entity.model.Event;
import com.uniwork.entity.model.Project;
import com.uniwork.entity.model.Task;
import com.uniwork.repository.EventRepository;
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
    private final EventRepository eventRepository;

    public ReportService(TaskRepository taskRepository, ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository, EventRepository eventRepository) {
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.eventRepository = eventRepository;
    }

    public ResponseEntity<List<ProjectReportDTO>> getProjectReport(Long userId, Long begin, Long end) {
        List<Long> projectIds = projectMemberRepository.findProjectIdsByUserId(userId);

        List<CompletableFuture<ProjectReportDTO>> futures = projectIds.stream()
                .map(projectId -> CompletableFuture.supplyAsync(() -> {
                    Project project = projectRepository.findProjectByProjectId(projectId);

                    CompletableFuture<Long> totalTask =
                            CompletableFuture.supplyAsync(() -> taskRepository.countAllByProjectId(projectId));
                    CompletableFuture<Long> completedTask =
                            CompletableFuture.supplyAsync(() -> taskRepository.countAllByProjectIdAndStatus(projectId, TaskStatus.COMPLETED));
                    CompletableFuture<Long> pendingTask =
                            CompletableFuture.supplyAsync(() -> taskRepository.countAllByProjectIdAndStatus(projectId, TaskStatus.PENDING));
                    CompletableFuture<Long> doingTask =
                            CompletableFuture.supplyAsync(() -> taskRepository.countAllByProjectIdAndStatus(projectId, TaskStatus.DOING));
                    CompletableFuture<Long> countMember =
                            CompletableFuture.supplyAsync(() -> projectMemberRepository.countUserIdByProjectId(projectId));

                    CompletableFuture.allOf(totalTask, completedTask, pendingTask, doingTask, countMember).join();

                    try {
                        Long total = totalTask.get();
                        Long completed = completedTask.get();
                        Long pending = pendingTask.get();
                        Long doing = doingTask.get();
                        Double completedPercent = total == 0 ? 0.0 : (completed * 100) / total;
                        Long count = countMember.get();
                        return new ProjectReportDTO(project, total, completed, pending, doing, completedPercent, count);
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
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedToAndStatus(userId, TaskStatus.COMPLETED));
        CompletableFuture<Long> pendingTask =
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedToAndStatus(userId, TaskStatus.PENDING));
        CompletableFuture<Long> doingTask =
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedToAndStatus(userId, TaskStatus.DOING));
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

    public ResponseEntity getPendingTask(Long userId, Long begin, Long end) {
        CompletableFuture<List<Task>> pendingTask =
                CompletableFuture.supplyAsync(() -> taskRepository.findAllByAssignedToAndStatus(userId, TaskStatus.PENDING));
        return ResponseEntity.ok(pendingTask.join());
    }

    public ResponseEntity getTasksPerformance(Long userId, Long begin, Long end) {
        CompletableFuture<Long> totalTasks =
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedTo(userId));
        CompletableFuture<Long> completedTask =
                CompletableFuture.supplyAsync(() -> taskRepository.countTasksCompletedBeforeDeadline(userId, TaskStatus.COMPLETED));

        CompletableFuture.allOf(totalTasks, completedTask).join();
        try {
            Long total = totalTasks.get();
            Long completed = completedTask.get();
            Double performancePercent = total == 0 ? 0.0 : (completed * 100) / total;
            TaskPerformanceDTO taskPerformanceDTO = new TaskPerformanceDTO(userId, total, completed, performancePercent);
            return ResponseEntity.ok(taskPerformanceDTO);
        }catch (Exception e) {
            throw new RuntimeException("Error while generating report for userId=" + userId, e);
        }
    }

    public ResponseEntity getUpcomingEvents(Long userId, Long begin, Long end) {
        CompletableFuture<List<Event>> upComingEvent =
                CompletableFuture.supplyAsync(() -> eventRepository.findAll());
        return ResponseEntity.ok(upComingEvent.join());
    }
}
