package com.uniwork.service;

import com.uniwork.entity.dto.ProjectReportDTO;
import com.uniwork.entity.dto.TaskPerformanceDTO;
import com.uniwork.entity.dto.TaskReportDTO;
import com.uniwork.entity.enumuration.ProjectStatus;
import com.uniwork.entity.enumuration.TaskStatus;
import com.uniwork.entity.model.Event;
import com.uniwork.entity.model.Project;
import com.uniwork.entity.model.Task;
import com.uniwork.entity.response.StatsResponse;
import com.uniwork.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportService {

    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ReportService(TaskRepository taskRepository, ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectReportDTO> getProjectReport(Long userId, Long begin, Long end) {
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

        return reports;
    }

    public TaskReportDTO getTaskReport(Long userId, Long begin, Long end) {

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
            return taskReportDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating task report for userId=" + userId, e);
        }
    }

    public List<Task> getPendingTask(Long userId, Long begin, Long end) {
        CompletableFuture<List<Task>> pendingTask =
                CompletableFuture.supplyAsync(() -> taskRepository.findAllByAssignedToAndStatus(userId, TaskStatus.PENDING));
        return pendingTask.join();
    }

    public TaskPerformanceDTO getTasksPerformance(Long userId, Long begin, Long end) {
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
            return taskPerformanceDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating report for userId=" + userId, e);
        }
    }

    public List<Event> getUpcomingEvents(Long userId, Long begin, Long end) {
        CompletableFuture<List<Event>> upComingEvent =
                CompletableFuture.supplyAsync(() -> eventRepository.findAll());
        return upComingEvent.join();
    }

    public List<StatsResponse> getStats(Long userId) {

        LocalDateTime now = LocalDateTime.now();
        CompletableFuture<Long> activeProjects =
                CompletableFuture.supplyAsync(() -> projectRepository.countProjectIdByStatusNot(ProjectStatus.ON_HOLD));
        CompletableFuture<Long> newProjectThisMonth =
                CompletableFuture.supplyAsync(() ->
                        projectRepository.countByCreatedDateBetween(
                                now.withDayOfMonth(1).toLocalDate().atStartOfDay(),
                                now
                        )
                );

        // 3️⃣ Task đã hoàn thành
        CompletableFuture<Long> completedTasks =
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedToAndStatus(userId, TaskStatus.COMPLETED));

        // 4️⃣ Task hoàn thành trong tuần này
        CompletableFuture<Long> newTasksThisWeek =
                CompletableFuture.supplyAsync(() ->
                        taskRepository.countByAssignedToAndUpdatedDateBetweenAndStatus(userId,
                                now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay(),
                                now, TaskStatus.COMPLETED
                        )
                );

        // 5️⃣ Tổng thành viên
        CompletableFuture<Long> teamMembers =
                CompletableFuture.supplyAsync(() -> userRepository.countAll());

        // 6️⃣ Thành viên mới trong tháng
        CompletableFuture<Long> newMembers =
                CompletableFuture.supplyAsync(() ->
                        userRepository.countByCreatedDateBetween(
                                now.withDayOfMonth(1).toLocalDate().atStartOfDay(),
                                now
                        )
                );

        // 7️⃣ Task đang pending
        CompletableFuture<Long> pendingTasks =
                CompletableFuture.supplyAsync(() -> taskRepository.countAllByAssignedToAndStatus(userId, TaskStatus.PENDING));

        // 8️⃣ Task pending của tuần trước (so sánh)
        CompletableFuture<Long> pendingTasksLastWeek =
                CompletableFuture.supplyAsync(() ->
                        taskRepository.countByAssignedToAndUpdatedDateBetweenAndStatus( userId,
                                now.minusWeeks(1).with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay(),
                                now.minusWeeks(1).with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59), TaskStatus.PENDING)
                );

        CompletableFuture.allOf(
                activeProjects, newProjectThisMonth,
                completedTasks, newTasksThisWeek,
                teamMembers, newMembers,
                pendingTasks, pendingTasksLastWeek).join();

        try {
            return List.of(
                    StatsResponse.builder()
                            .title("Active Projects")
                            .value(activeProjects.get())
                            .change("+" + newProjectThisMonth.get() + " this month")
                            .build(),
                    StatsResponse.builder()
                            .title("Tasks Completed")
                            .value(completedTasks.get())
                            .change("+" + newTasksThisWeek.get() + " this week")
                            .build(),
                    StatsResponse.builder()
                            .title("Team Members")
                            .value(teamMembers.get())
                            .change("+" + newMembers.get() + " new")
                            .build(),
                    StatsResponse.builder()
                            .title("Pending Tasks")
                            .value(pendingTasks.get())
                            .change("-" + (pendingTasksLastWeek.get() - pendingTasks.get()) + " from last week")
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error building stats response", e);
        }
    }

}
