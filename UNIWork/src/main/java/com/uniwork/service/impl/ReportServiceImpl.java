package com.uniwork.service.impl;

import com.uniwork.model.dto.ProjectReportDTO;
import com.uniwork.model.dto.TaskPerformanceDTO;
import com.uniwork.model.dto.TaskReportDTO;
import com.uniwork.model.enumuration.ProjectStatus;
import com.uniwork.model.enumuration.TaskStatus;
import com.uniwork.model.entity.Event;
import com.uniwork.model.entity.Project;
import com.uniwork.model.entity.Task;
import com.uniwork.model.projection.*;
import com.uniwork.model.response.StatsResponse;
import com.uniwork.repository.*;
import com.uniwork.service.ReportService;
import org.springframework.cache.annotation .Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ReportServiceImpl(TaskRepository taskRepository, ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public long countProjectsByUserId(Long userId) {
        return projectMemberRepository.countByUserId(userId);
    }

    @Cacheable(
            value = "uniwork:project:report",
            key = "'user:' + #userId + ':p:' + #page + ':s:' + #size"
    )

    public List<ProjectReportDTO> getProjectReport(Long userId, Long begin, Long end, int page, int size) {
        List<Long> allProjectIds = projectMemberRepository.findProjectIdsByUserId(userId);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allProjectIds.size());
        List<Long> pagedProjectIds = allProjectIds.subList(startIndex, endIndex);

        CompletableFuture<Map<Long, Project>> projectMapFuture =
                CompletableFuture.supplyAsync(() -> projectRepository.findAllById(pagedProjectIds)
                        .stream()
                        .collect(Collectors.toMap(project -> project.getProjectId(), project -> project)));

        CompletableFuture<Map<Long, ReportProjectProjection>> reportProjectionMapFuture =
                CompletableFuture.supplyAsync(() -> taskRepository.reportProjects(pagedProjectIds)
                        .stream()
                        .collect(Collectors.toMap(ReportProjectProjection::getProjectId, projection -> projection)));

        CompletableFuture.allOf(projectMapFuture, reportProjectionMapFuture).join();

        Map<Long, Project> projectMap = projectMapFuture.join();
        Map<Long, ReportProjectProjection> reportProjectionMap = reportProjectionMapFuture.join();
        return pagedProjectIds.stream().map(id -> {
            Project project = projectMap.get(id);
            ReportProjectProjection projection = reportProjectionMap.get(id);
            Long total = projection != null ? projection.getTotalTasks() - projection.getCancelledTasks() : 0L;
            Long completed = projection != null ? projection.getCompletedTasks() + projection.getReviewingTasks() : 0L;
            Long pending = projection != null ? projection.getPendingTasks() : 0L;
            Long doing = projection != null ? projection.getDoingTasks() : 0L;
            Double completedPercent = total == 0 ? 0.0 : (completed * 100) / total;
            Long count = projection != null ? projection.getTotalMembers() : 0L;
            return new ProjectReportDTO(project, total, completed, pending, doing, completedPercent, count);
        }).toList();
    }

    public Page<ProjectReportDTO> getProjectReportV2(Long userId, Long begin, Long end, Pageable pageable) {
        Page<Long> projectIdPage = projectMemberRepository.findProjectIdsByUserId(userId, pageable);

        List<Long> projectIds = projectIdPage.getContent();

        if (projectIds.isEmpty()) {
            return Page.empty(pageable);
        }

        CompletableFuture<Map<Long, Project>> projectMapFuture =
                CompletableFuture.supplyAsync(() ->
                        projectRepository.findAllById(projectIds)
                                .stream()
                                .collect(Collectors.toMap(
                                        Project::getProjectId,
                                        p -> p
                                ))
                );

        CompletableFuture<Map<Long, ReportProjectProjection>> reportProjectionMapFuture =
                CompletableFuture.supplyAsync(() ->
                        taskRepository.reportProjects(projectIds)
                                .stream()
                                .collect(Collectors.toMap(
                                        ReportProjectProjection::getProjectId,
                                        p -> p
                                ))
                );

        CompletableFuture.allOf(projectMapFuture, reportProjectionMapFuture).join();

        Map<Long, Project> projectMap = projectMapFuture.join();
        Map<Long, ReportProjectProjection> reportMap = reportProjectionMapFuture.join();

        // ✅ Gom project cần update để batch save
        List<Project> projectsToUpdate = new ArrayList<>();

        List<ProjectReportDTO> result = projectIds.stream()
                .map(id -> {
                    Project project = projectMap.get(id);
                    ReportProjectProjection projection = reportMap.get(id);

                    Long total = projection != null
                            ? projection.getTotalTasks() - projection.getCancelledTasks()
                            : 0L;

                    Long completed = projection != null
                            ? projection.getCompletedTasks() + projection.getReviewingTasks()
                            : 0L;

                    Long pending = projection != null ? projection.getPendingTasks() : 0L;
                    Long doing = projection != null ? projection.getDoingTasks() : 0L;

                    double completedPercent =
                            total == 0 ? 0.0 : Math.round((completed * 100.0) / total);

                    Long memberCount =
                            projection != null ? projection.getTotalMembers() : 0L;

                    // ✅ Tính status động
                    ProjectStatus status = ProjectStatus.fromProgress(completedPercent, total, pending, doing);

                    // ✅ Nếu status khác DB thì update
                    if (project != null && (project.getStatus() == null || !project.getStatus().equals(status))) {
                        project.setStatus(status);
                        projectsToUpdate.add(project);
                    }

                    return new ProjectReportDTO(
                            project,
                            total,
                            completed,
                            pending,
                            doing,
                            completedPercent,
                            memberCount
                    );
                })
                .toList();

        // ✅ Batch update một lần
        if (!projectsToUpdate.isEmpty()) {
            projectRepository.saveAll(projectsToUpdate);
        }

        return new PageImpl<>(result, pageable, projectIdPage.getTotalElements());
    }


    public TaskReportDTO getTaskReport(Long userId, Long begin, Long end) {

        ReportTaskProjection p = taskRepository.getTaskReport(userId);

        long total = p != null ? p.getTotalTasks() - p.getCancelledTasks() : 0;
        long completed = p != null ? p.getCompletedTasks() + p.getReviewingTasks(): 0;
        long pending = p != null ? p.getPendingTasks() : 0;
        long doing = p != null ? p.getDoingTasks() : 0;

        double completedPercent = total == 0 ? 0 : (completed * 100.0) / total;

        return new TaskReportDTO(
                total,
                completed,
                pending,
                doing,
                completedPercent
        );

    }

    public List<Task> getPendingTask(Long userId, Long begin, Long end) {
        List<TaskStatus> pendingStatuses = List.of(TaskStatus.PENDING, TaskStatus.DOING, TaskStatus.REVIEWING);
        CompletableFuture<List<Task>> pendingTask =
                CompletableFuture.supplyAsync(() -> taskRepository.findAllByAssignedToAndStatusIn(userId, pendingStatuses));
        return pendingTask.join();
    }

    public Page<Task> getPendingTask(Long userId, Long begin, Long end, Pageable pageable) {
        List<TaskStatus> pendingStatuses = List.of(TaskStatus.PENDING, TaskStatus.DOING, TaskStatus.REVIEWING);
        CompletableFuture<Page<Task>> pendingTask =
                CompletableFuture.supplyAsync(() -> taskRepository.findAllByAssignedToAndStatusIn(userId, pendingStatuses, pageable));
        return pendingTask.join();
    }

    public TaskPerformanceDTO getTasksPerformance(Long userId, Long begin, Long end) {
        ReportTaskPerformanceProjection p = taskRepository.getTasksPerformance(userId);
        long total = p != null ? p.getTotalTasks() : 0;
        long completedBeforeDeadline = p != null ? p.getCompletedBeforeDeadline() : 0;
        long remaining = total - completedBeforeDeadline;
        double performancePercent = total == 0 ? 0 : (completedBeforeDeadline * 100.0) / total;
        return new TaskPerformanceDTO(
                userId,
                total,
                completedBeforeDeadline,
                remaining,
                performancePercent
        );
    }

    public List<Event> getUpcomingEvents(Long userId, Long begin, Long end) {
        CompletableFuture<List<Event>> upComingEvent =
                CompletableFuture.supplyAsync(() -> eventRepository.findAll());
        return upComingEvent.join();
    }

    public List<StatsResponse> getStats(Long userId) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime startOfLastWeek = now.minusWeeks(1).with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime endOfLastWeek = now.minusWeeks(1).with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59);

        CompletableFuture<ReportProjectStatsProjection> projectStats =
                CompletableFuture.supplyAsync(() -> projectRepository.getProjectStats(startOfMonth, now));

        CompletableFuture<ReportUserStatsProjection> userStats =
                CompletableFuture.supplyAsync(() -> userRepository.getUserStats(startOfMonth, now));

        CompletableFuture<ReportTaskStatsProjection> taskStats =
                CompletableFuture.supplyAsync(() -> taskRepository.getTaskStats(
                        userId, startOfWeek, now, startOfLastWeek, endOfLastWeek));

        CompletableFuture.allOf(projectStats, userStats, taskStats).join();

        try {
            ReportProjectStatsProjection p = projectStats.get();
            ReportUserStatsProjection u = userStats.get();
            ReportTaskStatsProjection t = taskStats.get();

            return List.of(
                    new StatsResponse("Active Projects", p.getActiveProjects(), "+" + p.getNewProjectThisMonth() + " this month"),
                    new StatsResponse("Tasks Completed", t.getCompletedTasks(), "+" + t.getNewTasksThisWeek() + " this week"),
                    new StatsResponse("Team Members", u.getTeamMembers(), "+" + u.getNewMembers() + " new"),
                    new StatsResponse("Pending Tasks", t.getPendingTasks(),
                            "-" + (t.getPendingTasksLastWeek() - t.getPendingTasks()) + " from last week")
            );
        } catch (Exception e) {
            throw new RuntimeException("Error building stats response", e);
        }
    }


}
