package com.uniwork.modules.report.service;

import com.uniwork.modules.report.dto.*;
import com.uniwork.modules.task.dto.TaskPerformanceDTO;
import com.uniwork.enums.IssueStatus;
import com.uniwork.enums.ProjectStatus;
import com.uniwork.enums.TaskStatus;
import com.uniwork.modules.event.entity.Event;
import com.uniwork.modules.project.entity.Project;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.stage.entity.Stage;
import com.uniwork.modules.report.projection.*;
import com.uniwork.modules.report.response.StatsResponse;
import com.uniwork.modules.task.repository.TaskRepository;
import com.uniwork.modules.project.repository.ProjectMemberRepository;
import com.uniwork.modules.project.repository.ProjectRepository;
import com.uniwork.modules.event.repository.EventRepository;
import com.uniwork.modules.user.repository.UserRepository;
import com.uniwork.modules.issue.repository.IssueRepository;
import com.uniwork.modules.issue.projection.ReportIssueProjection;
import com.uniwork.modules.issue.projection.IssueDetailProjection;
import com.uniwork.modules.stage.repository.StageRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final IssueRepository issueRepository;
    private final StageRepository stageRepository;

    public ReportServiceImpl(TaskRepository taskRepository, ProjectMemberRepository projectMemberRepository,
                             ProjectRepository projectRepository, EventRepository eventRepository,
                             UserRepository userRepository, IssueRepository issueRepository,
                             StageRepository stageRepository) {
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
        this.stageRepository = stageRepository;
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

        // Gom project cần update để batch save
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

                    // Tính status động
                    ProjectStatus status = ProjectStatus.fromProgress(completedPercent, total, pending, doing);

                    // Nếu status khác DB thì update
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

        // Batch update một lần
        if (!projectsToUpdate.isEmpty()) {
            projectRepository.saveAll(projectsToUpdate);
        }

        return new PageImpl<>(result, pageable, projectIdPage.getTotalElements());
    }


    public TaskReportDTO getTaskReport(Long userId, Long begin, Long end) {
        ReportTaskProjection p = taskRepository.getTaskReport(userId);
        ReportIssueProjection ip = issueRepository.getIssueReport(userId);

        long total = p != null ? p.getTotalTasks() - p.getCancelledTasks() : 0;
        long completed = p != null ? p.getCompletedTasks() + p.getReviewingTasks(): 0;
        long pending = p != null ? p.getPendingTasks() : 0;
        long doing = p != null ? p.getDoingTasks() : 0;
        double completedPercent = total == 0 ? 0 : (completed * 100.0) / total;

        long totalIssues = ip != null ? ip.getTotalIssues() : 0;
        long completedIssues = ip != null ? ip.getCompletedIssues() : 0;
        long pendingIssues = ip != null ? ip.getPendingIssues() : 0;
        long doingIssues = ip != null ? ip.getDoingIssues() : 0;
        double issuesCompletedPercent = totalIssues == 0 ? 0 : (completedIssues * 100.0) / totalIssues;

        return new TaskReportDTO(
                total,
                completed,
                pending,
                doing,
                completedPercent,
                totalIssues,
                completedIssues,
                pendingIssues,
                doingIssues,
                issuesCompletedPercent
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

    @Override
    public Page<IssueDetailProjection> getPendingIssues(Long userId, Long begin, Long end, Pageable pageable) {
        List<IssueStatus> pendingStatuses = List.of(IssueStatus.OPEN, IssueStatus.IN_PROGRESS, IssueStatus.REOPENED);
        return issueRepository.findByAssignedToAndStatusInWithDetails(userId, pendingStatuses, pageable);
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

    // =====================================================
    // NEW REPORT APIs
    // =====================================================

    @Override
    public SingleProjectReportDTO getProjectDetailReport(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Task stats
        ReportProjectProjection taskStats = taskRepository.reportProjects(List.of(projectId))
                .stream().findFirst().orElse(null);

        long totalTasks = taskStats != null ? taskStats.getTotalTasks() : 0;
        long completedTasks = taskStats != null ? taskStats.getCompletedTasks() : 0;
        long pendingTasks = taskStats != null ? taskStats.getPendingTasks() : 0;
        long doingTasks = taskStats != null ? taskStats.getDoingTasks() : 0;
        long reviewingTasks = taskStats != null ? taskStats.getReviewingTasks() : 0;
        long cancelledTasks = taskStats != null ? taskStats.getCancelledTasks() : 0;
        long effectiveTotal = totalTasks - cancelledTasks;
        double taskCompletedPercent = effectiveTotal == 0 ? 0 : Math.round((completedTasks * 100.0) / effectiveTotal);

        // Issue stats
        Long totalIssues = issueRepository.countByProjectId(projectId);
        Long openIssues = issueRepository.countByProjectIdAndStatus(projectId, IssueStatus.OPEN);
        Long inProgressIssues = issueRepository.countByProjectIdAndStatus(projectId, IssueStatus.IN_PROGRESS);
        Long resolvedIssues = issueRepository.countByProjectIdAndStatus(projectId, IssueStatus.RESOLVED);
        Long closedIssues = issueRepository.countByProjectIdAndStatus(projectId, IssueStatus.CLOSED);

        // Member count
        Long totalMembers = taskStats != null ? taskStats.getTotalMembers() : 0L;

        // Stage stats
        List<Stage> stages = stageRepository.findByProjectIdOrderByOrderIndexAsc(projectId);
        long totalStages = stages.size();
        long completedStages = stages.stream().filter(s -> s.getStatus() == com.uniwork.enums.StageStatus.COMPLETED).count();
        long activeStages = stages.stream().filter(s -> s.getStatus() == com.uniwork.enums.StageStatus.ACTIVE).count();

        return SingleProjectReportDTO.builder()
                .projectId(project.getProjectId())
                .projectName(project.getName())
                .projectStatus(project.getStatus() != null ? project.getStatus().name() : null)
                .projectMethod(project.getMethod() != null ? project.getMethod().name() : null)
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .totalTasks(effectiveTotal)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .doingTasks(doingTasks)
                .reviewingTasks(reviewingTasks)
                .cancelledTasks(cancelledTasks)
                .taskCompletedPercent(taskCompletedPercent)
                .totalIssues(totalIssues)
                .openIssues(openIssues)
                .inProgressIssues(inProgressIssues)
                .resolvedIssues(resolvedIssues)
                .closedIssues(closedIssues)
                .totalMembers(totalMembers)
                .totalStages(totalStages)
                .completedStages(completedStages)
                .activeStages(activeStages)
                .build();
    }

    @Override
    public List<MemberWorkloadDTO> getMemberWorkload(Long userId) {
        List<Long> projectIds = projectMemberRepository.findProjectIdsByUserId(userId);
        if (projectIds.isEmpty()) return List.of();

        // Get task workload
        Map<Long, ReportMemberWorkloadProjection> taskWorkloadMap = taskRepository.getMemberWorkload(projectIds)
                .stream()
                .collect(Collectors.toMap(ReportMemberWorkloadProjection::getUserId, p -> p, (a, b) -> a));

        // Get issue workload
        Map<Long, ReportMemberIssueWorkloadProjection> issueWorkloadMap = issueRepository.getMemberIssueWorkload(projectIds)
                .stream()
                .collect(Collectors.toMap(ReportMemberIssueWorkloadProjection::getUserId, p -> p, (a, b) -> a));

        // Merge all user IDs
        java.util.Set<Long> allUserIds = new java.util.HashSet<>();
        allUserIds.addAll(taskWorkloadMap.keySet());
        allUserIds.addAll(issueWorkloadMap.keySet());

        return allUserIds.stream().map(uid -> {
            ReportMemberWorkloadProjection tw = taskWorkloadMap.get(uid);
            ReportMemberIssueWorkloadProjection iw = issueWorkloadMap.get(uid);

            long totalTasks = tw != null ? tw.getTotalTasks() : 0;
            long completedTasks = tw != null ? tw.getCompletedTasks() : 0;
            long pendTasks = tw != null ? tw.getPendingTasks() : 0;
            long doTasks = tw != null ? tw.getDoingTasks() : 0;
            long totalIssues = iw != null ? iw.getTotalIssues() : 0;
            long completedIssues = iw != null ? iw.getCompletedIssues() : 0;
            long pendIssues = iw != null ? iw.getPendingIssues() : 0;
            double rate = totalTasks == 0 ? 0 : Math.round((completedTasks * 100.0) / totalTasks);
            String userName = tw != null ? tw.getUserName() : (iw != null ? iw.getUserName() : "Unknown");

            return MemberWorkloadDTO.builder()
                    .userId(uid)
                    .userName(userName)
                    .totalTasks(totalTasks)
                    .completedTasks(completedTasks)
                    .pendingTasks(pendTasks)
                    .doingTasks(doTasks)
                    .totalIssues(totalIssues)
                    .completedIssues(completedIssues)
                    .pendingIssues(pendIssues)
                    .taskCompletionRate(rate)
                    .build();
        }).toList();
    }

    @Override
    public List<OverdueItemDTO> getOverdueItems(Long userId) {
        List<OverdueItemDTO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Overdue tasks
        List<ReportOverdueTaskProjection> overdueTasks = taskRepository.findOverdueTasks(userId);
        for (ReportOverdueTaskProjection t : overdueTasks) {
            long daysOverdue = t.getDueDate() != null ? ChronoUnit.DAYS.between(t.getDueDate(), now) : 0;
            result.add(OverdueItemDTO.builder()
                    .id(t.getTaskId())
                    .type("TASK")
                    .title(t.getTitle())
                    .status(t.getStatus())
                    .priority(t.getPriority())
                    .dueDate(t.getDueDate())
                    .daysOverdue(daysOverdue)
                    .assigneeName(t.getAssigneeName())
                    .assignedTo(t.getAssignedTo())
                    .projectId(t.getProjectId())
                    .projectName(t.getProjectName())
                    .build());
        }

        // Overdue issues
        List<ReportOverdueIssueProjection> overdueIssues = issueRepository.findOverdueIssues(userId);
        for (ReportOverdueIssueProjection i : overdueIssues) {
            long daysOverdue = i.getDueDate() != null ? ChronoUnit.DAYS.between(i.getDueDate(), now) : 0;
            result.add(OverdueItemDTO.builder()
                    .id(i.getIssueId())
                    .type("ISSUE")
                    .title(i.getTitle())
                    .status(i.getStatus())
                    .priority(i.getPriority())
                    .dueDate(i.getDueDate())
                    .daysOverdue(daysOverdue)
                    .assigneeName(i.getAssigneeName())
                    .assignedTo(i.getAssignedTo())
                    .projectId(i.getProjectId())
                    .projectName(i.getProjectName())
                    .build());
        }

        // Sort by daysOverdue descending
        result.sort((a, b) -> Long.compare(b.getDaysOverdue(), a.getDaysOverdue()));
        return result;
    }

    @Override
    public List<StageReportDTO> getStageReport(Long userId, Long projectId) {
        List<Stage> stages = stageRepository.findByProjectIdOrderByOrderIndexAsc(projectId);

        return stages.stream().map(stage -> {
            Long totalTasks = taskRepository.countByStageId(stage.getStageId());
            Long completedTasks = taskRepository.countByStageIdAndStatus(stage.getStageId(), TaskStatus.COMPLETED);
            Long pendingTasks = taskRepository.countByStageIdAndStatus(stage.getStageId(), TaskStatus.PENDING);
            Long doingTasks = taskRepository.countByStageIdAndStatus(stage.getStageId(), TaskStatus.DOING);
            double progress = totalTasks > 0 ? Math.round((double) completedTasks / totalTasks * 10000.0) / 100.0 : 0.0;

            Long totalIssues = issueRepository.countByStageId(stage.getStageId());
            Long openIssues = issueRepository.countOpenByStageId(stage.getStageId());
            Long resolvedIssues = issueRepository.countResolvedByStageId(stage.getStageId());

            return StageReportDTO.builder()
                    .stageId(stage.getStageId())
                    .stageName(stage.getName())
                    .stageType(stage.getType() != null ? stage.getType().name() : null)
                    .stageStatus(stage.getStatus() != null ? stage.getStatus().name() : null)
                    .orderIndex(stage.getOrderIndex())
                    .startDate(stage.getStartDate())
                    .endDate(stage.getEndDate())
                    .totalTasks(totalTasks)
                    .completedTasks(completedTasks)
                    .pendingTasks(pendingTasks)
                    .doingTasks(doingTasks)
                    .progressPercent(progress)
                    .totalIssues(totalIssues)
                    .openIssues(openIssues)
                    .resolvedIssues(resolvedIssues)
                    .build();
        }).toList();
    }

    // =====================================================
    // ANALYTICS APIs
    // =====================================================

    @Override
    public CompletionTrendDTO getCompletionTrend(Long userId, String granularity, LocalDateTime from, LocalDateTime to) {
        List<ReportTimeSeriesProjection> taskData;
        List<ReportTimeSeriesProjection> issueData;

        switch (granularity.toUpperCase()) {
            case "MONTHLY" -> {
                taskData = taskRepository.getTaskCompletionByMonth(userId, from, to);
                issueData = issueRepository.getIssueCompletionByMonth(userId, from, to);
            }
            case "YEARLY" -> {
                taskData = taskRepository.getTaskCompletionByYear(userId, from, to);
                issueData = issueRepository.getIssueCompletionByYear(userId, from, to);
            }
            default -> { // DAILY
                taskData = taskRepository.getTaskCompletionByDay(userId, from, to);
                issueData = issueRepository.getIssueCompletionByDay(userId, from, to);
            }
        }

        List<TimeSeriesDataPoint> dataPoints = mergeTimeSeries(taskData, issueData);
        long totalTasks = dataPoints.stream().mapToLong(TimeSeriesDataPoint::getTasks).sum();
        long totalIssues = dataPoints.stream().mapToLong(TimeSeriesDataPoint::getIssues).sum();

        return CompletionTrendDTO.builder()
                .granularity(granularity.toUpperCase())
                .from(from.toString())
                .to(to.toString())
                .totalTasksCompleted(totalTasks)
                .totalIssuesCompleted(totalIssues)
                .dataPoints(dataPoints)
                .build();
    }

    @Override
    public CreationTrendDTO getCreationTrend(Long userId, String granularity, LocalDateTime from, LocalDateTime to) {
        List<ReportTimeSeriesProjection> taskData;
        List<ReportTimeSeriesProjection> issueData;

        switch (granularity.toUpperCase()) {
            case "MONTHLY" -> {
                taskData = taskRepository.getTaskCreationByMonth(userId, from, to);
                issueData = issueRepository.getIssueCreationByMonth(userId, from, to);
            }
            case "YEARLY" -> {
                taskData = taskRepository.getTaskCreationByYear(userId, from, to);
                issueData = issueRepository.getIssueCreationByYear(userId, from, to);
            }
            default -> { // DAILY
                taskData = taskRepository.getTaskCreationByDay(userId, from, to);
                issueData = issueRepository.getIssueCreationByDay(userId, from, to);
            }
        }

        List<TimeSeriesDataPoint> dataPoints = mergeTimeSeries(taskData, issueData);
        long totalTasks = dataPoints.stream().mapToLong(TimeSeriesDataPoint::getTasks).sum();
        long totalIssues = dataPoints.stream().mapToLong(TimeSeriesDataPoint::getIssues).sum();

        return CreationTrendDTO.builder()
                .granularity(granularity.toUpperCase())
                .from(from.toString())
                .to(to.toString())
                .totalTasksCreated(totalTasks)
                .totalIssuesCreated(totalIssues)
                .dataPoints(dataPoints)
                .build();
    }

    @Override
    public AnalyticsSummaryDTO getAnalyticsSummary(Long userId) {
        // Counts
        CompletableFuture<Long> totalTasksFuture = CompletableFuture.supplyAsync(() -> taskRepository.countAllByUser(userId));
        CompletableFuture<Long> completedTasksFuture = CompletableFuture.supplyAsync(() -> taskRepository.countCompletedByUser(userId));
        CompletableFuture<Long> totalIssuesFuture = CompletableFuture.supplyAsync(() -> issueRepository.countAllByUser(userId));
        CompletableFuture<Long> completedIssuesFuture = CompletableFuture.supplyAsync(() -> issueRepository.countCompletedByUser(userId));
        CompletableFuture<Long> overdueTasksFuture = CompletableFuture.supplyAsync(() -> taskRepository.countOverdueTasks(userId));
        CompletableFuture<Long> overdueIssuesFuture = CompletableFuture.supplyAsync(() -> issueRepository.countOverdueIssues(userId));
        CompletableFuture<Long> totalProjectsFuture = CompletableFuture.supplyAsync(() -> projectMemberRepository.countByUserId(userId));

        // Averages
        CompletableFuture<ReportAvgCompletionProjection> avgTaskFuture = CompletableFuture.supplyAsync(() -> taskRepository.getAvgTaskCompletionTime(userId));
        CompletableFuture<ReportAvgCompletionProjection> avgIssueFuture = CompletableFuture.supplyAsync(() -> issueRepository.getAvgIssueResolutionTime(userId));

        // Distributions
        CompletableFuture<List<ReportStatusCountProjection>> taskStatusFuture = CompletableFuture.supplyAsync(() -> taskRepository.getTaskStatusDistribution(userId));
        CompletableFuture<List<ReportStatusCountProjection>> issueStatusFuture = CompletableFuture.supplyAsync(() -> issueRepository.getIssueStatusDistribution(userId));
        CompletableFuture<List<ReportPriorityCountProjection>> taskPriorityFuture = CompletableFuture.supplyAsync(() -> taskRepository.getTaskPriorityDistribution(userId));
        CompletableFuture<List<ReportPriorityCountProjection>> issuePriorityFuture = CompletableFuture.supplyAsync(() -> issueRepository.getIssuePriorityDistribution(userId));

        CompletableFuture.allOf(
                totalTasksFuture, completedTasksFuture, totalIssuesFuture, completedIssuesFuture,
                overdueTasksFuture, overdueIssuesFuture, totalProjectsFuture,
                avgTaskFuture, avgIssueFuture,
                taskStatusFuture, issueStatusFuture, taskPriorityFuture, issuePriorityFuture
        ).join();

        long totalTasks = totalTasksFuture.join();
        long completedTasks = completedTasksFuture.join();
        long totalIssues = totalIssuesFuture.join();
        long completedIssues = completedIssuesFuture.join();
        long overdueTasks = overdueTasksFuture.join();
        long overdueIssues = overdueIssuesFuture.join();
        long totalProjects = totalProjectsFuture.join();

        double taskCompletionRate = totalTasks > 0 ? Math.round((completedTasks * 100.0) / totalTasks * 100.0) / 100.0 : 0;
        double issueCompletionRate = totalIssues > 0 ? Math.round((completedIssues * 100.0) / totalIssues * 100.0) / 100.0 : 0;
        long totalActive = (totalTasks - completedTasks) + (totalIssues - completedIssues);
        double overdueRate = totalActive > 0 ? Math.round(((overdueTasks + overdueIssues) * 100.0) / totalActive * 100.0) / 100.0 : 0;

        ReportAvgCompletionProjection avgTask = avgTaskFuture.join();
        ReportAvgCompletionProjection avgIssue = avgIssueFuture.join();
        Double avgTaskDays = avgTask != null ? avgTask.getAvgDays() : null;
        Double avgIssueDays = avgIssue != null ? avgIssue.getAvgDays() : null;

        List<StatusDistributionDTO> taskStatusDist = toStatusDistribution(taskStatusFuture.join());
        List<StatusDistributionDTO> issueStatusDist = toStatusDistribution(issueStatusFuture.join());
        List<PriorityDistributionDTO> taskPriorityDist = toPriorityDistribution(taskPriorityFuture.join());
        List<PriorityDistributionDTO> issuePriorityDist = toPriorityDistribution(issuePriorityFuture.join());

        return AnalyticsSummaryDTO.builder()
                .totalTasks(totalTasks)
                .totalIssues(totalIssues)
                .totalProjects(totalProjects)
                .completedTasks(completedTasks)
                .completedIssues(completedIssues)
                .taskCompletionRate(taskCompletionRate)
                .issueCompletionRate(issueCompletionRate)
                .overdueTasks(overdueTasks)
                .overdueIssues(overdueIssues)
                .overdueRate(overdueRate)
                .avgTaskCompletionDays(avgTaskDays)
                .avgIssueResolutionDays(avgIssueDays)
                .taskStatusDistribution(taskStatusDist)
                .issueStatusDistribution(issueStatusDist)
                .taskPriorityDistribution(taskPriorityDist)
                .issuePriorityDistribution(issuePriorityDist)
                .build();
    }

    @Override
    public ProjectAnalyticsDTO getProjectAnalytics(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        // Task stats
        ReportProjectProjection taskStats = taskRepository.reportProjects(List.of(projectId))
                .stream().findFirst().orElse(null);

        long totalTasks = taskStats != null ? taskStats.getTotalTasks() : 0;
        long completedTasks = taskStats != null ? taskStats.getCompletedTasks() : 0;
        double taskCompletionRate = totalTasks > 0 ? Math.round((completedTasks * 100.0) / totalTasks * 100.0) / 100.0 : 0;

        // Issue stats
        Long totalIssues = issueRepository.countByProjectId(projectId);
        Long resolvedIssues = issueRepository.countByProjectIdAndStatus(projectId, IssueStatus.RESOLVED)
                + issueRepository.countByProjectIdAndStatus(projectId, IssueStatus.CLOSED);
        double issueResolutionRate = totalIssues > 0 ? Math.round((resolvedIssues * 100.0) / totalIssues * 100.0) / 100.0 : 0;

        // Velocity
        Long tasksCompletedThisWeek = taskRepository.countCompletedTasksInPeriod(projectId, startOfWeek, now);
        Long tasksCompletedThisMonth = taskRepository.countCompletedTasksInPeriod(projectId, startOfMonth, now);
        Long issuesResolvedThisWeek = issueRepository.countResolvedIssuesInPeriod(projectId, startOfWeek, now);
        Long issuesResolvedThisMonth = issueRepository.countResolvedIssuesInPeriod(projectId, startOfMonth, now);

        // Days remaining
        Long daysRemaining = null;
        if (project.getEndDate() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(now, project.getEndDate());
            daysRemaining = Math.max(0, days);
        }

        // Daily velocity = completed tasks / days elapsed since project start
        double dailyTaskVelocity = 0;
        if (project.getStartDate() != null) {
            long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(project.getStartDate(), now);
            if (daysElapsed > 0) {
                dailyTaskVelocity = Math.round((completedTasks * 100.0) / daysElapsed) / 100.0;
            }
        }

        // Distributions
        List<StatusDistributionDTO> taskStatusDist = toStatusDistribution(taskRepository.getTaskStatusDistributionByProject(projectId));
        List<StatusDistributionDTO> issueStatusDist = toStatusDistribution(issueRepository.getIssueStatusDistributionByProject(projectId));

        return ProjectAnalyticsDTO.builder()
                .projectId(projectId)
                .projectName(project.getName())
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .taskCompletionRate(taskCompletionRate)
                .totalIssues(totalIssues)
                .resolvedIssues(resolvedIssues)
                .issueResolutionRate(issueResolutionRate)
                .tasksCompletedThisWeek(tasksCompletedThisWeek)
                .tasksCompletedThisMonth(tasksCompletedThisMonth)
                .issuesResolvedThisWeek(issuesResolvedThisWeek)
                .issuesResolvedThisMonth(issuesResolvedThisMonth)
                .projectStartDate(project.getStartDate())
                .projectEndDate(project.getEndDate())
                .daysRemaining(daysRemaining)
                .dailyTaskVelocity(dailyTaskVelocity)
                .taskStatusDistribution(taskStatusDist)
                .issueStatusDistribution(issueStatusDist)
                .build();
    }

    @Override
    public List<StatusDistributionDTO> getTaskStatusDistribution(Long userId, Long projectId) {
        List<ReportStatusCountProjection> data;
        if (projectId != null) {
            data = taskRepository.getTaskStatusDistributionByProject(projectId);
        } else {
            data = taskRepository.getTaskStatusDistribution(userId);
        }
        return toStatusDistribution(data);
    }

    @Override
    public List<StatusDistributionDTO> getIssueStatusDistribution(Long userId, Long projectId) {
        List<ReportStatusCountProjection> data;
        if (projectId != null) {
            data = issueRepository.getIssueStatusDistributionByProject(projectId);
        } else {
            data = issueRepository.getIssueStatusDistribution(userId);
        }
        return toStatusDistribution(data);
    }

    @Override
    public List<PriorityDistributionDTO> getTaskPriorityDistribution(Long userId) {
        return toPriorityDistribution(taskRepository.getTaskPriorityDistribution(userId));
    }

    @Override
    public List<PriorityDistributionDTO> getIssuePriorityDistribution(Long userId) {
        return toPriorityDistribution(issueRepository.getIssuePriorityDistribution(userId));
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    private List<TimeSeriesDataPoint> mergeTimeSeries(
            List<ReportTimeSeriesProjection> taskData,
            List<ReportTimeSeriesProjection> issueData) {

        // Index issue data by label
        Map<String, Long> issueMap = issueData.stream()
                .collect(Collectors.toMap(
                        ReportTimeSeriesProjection::getLabel,
                        ReportTimeSeriesProjection::getIssueCount,
                        (a, b) -> a + b
                ));

        // Collect all labels
        java.util.Set<String> allLabels = new java.util.LinkedHashSet<>();
        taskData.forEach(d -> allLabels.add(d.getLabel()));
        issueData.forEach(d -> allLabels.add(d.getLabel()));

        // Index task data by label
        Map<String, Long> taskMap = taskData.stream()
                .collect(Collectors.toMap(
                        ReportTimeSeriesProjection::getLabel,
                        ReportTimeSeriesProjection::getTaskCount,
                        (a, b) -> a + b
                ));

        return allLabels.stream()
                .sorted()
                .map(label -> TimeSeriesDataPoint.builder()
                        .label(label)
                        .tasks(taskMap.getOrDefault(label, 0L))
                        .issues(issueMap.getOrDefault(label, 0L))
                        .build())
                .toList();
    }

    private List<StatusDistributionDTO> toStatusDistribution(List<ReportStatusCountProjection> data) {
        long total = data.stream().mapToLong(ReportStatusCountProjection::getCount).sum();
        return data.stream().map(d -> StatusDistributionDTO.builder()
                .status(d.getStatus())
                .count(d.getCount())
                .percentage(total > 0 ? Math.round((d.getCount() * 100.0) / total * 100.0) / 100.0 : 0)
                .build()
        ).toList();
    }

    private List<PriorityDistributionDTO> toPriorityDistribution(List<ReportPriorityCountProjection> data) {
        long total = data.stream().mapToLong(ReportPriorityCountProjection::getCount).sum();
        return data.stream().map(d -> PriorityDistributionDTO.builder()
                .priority(d.getPriority())
                .count(d.getCount())
                .percentage(total > 0 ? Math.round((d.getCount() * 100.0) / total * 100.0) / 100.0 : 0)
                .build()
        ).toList();
    }

}
