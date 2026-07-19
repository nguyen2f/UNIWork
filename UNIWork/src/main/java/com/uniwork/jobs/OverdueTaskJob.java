//package com.uniwork.jobs;
//
//import com.uniwork.enums.NotificationEntityType;
//import com.uniwork.enums.NotificationType;
//import com.uniwork.modules.notification.dto.NotificationDTO;
//import com.uniwork.modules.notification.service.NotificationService;
//import com.uniwork.modules.task.entity.Task;
//import com.uniwork.modules.task.repository.TaskRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Component
//public class OverdueTaskJob {
//
//    @Autowired
//    private TaskRepository taskRepository;
//
//    @Autowired
//    private NotificationService notificationService;
//
//    // Run every day at 00:05
//    @Scheduled(cron = "0 5 0 * * ?")
//    @Transactional
//    public void checkAndNotifyOverdueTasks() {
//        log.info("Running OverdueTaskJob to check for overdue tasks...");
//        List<Task> overdueTasks = taskRepository.findAllOverdueTasks();
//
//        int count = 0;
//        for (Task task : overdueTasks) {
//            if (task.getAssignedTo() != null) {
//                NotificationDTO notificationDTO = NotificationDTO.builder()
//                        .type(NotificationType.SYSTEM_ALERT)
//                        .entityType(NotificationEntityType.TASK)
//                        .entityId(task.getTaskId())
//                        .createdAt(LocalDateTime.now())
//                        .message("Task '" + task.getTitle() + "' is overdue! Please check and update its status.")
//                        .build();
//
//                notificationService.sendNotification(task.getAssignedTo(), notificationDTO);
//                count++;
//            }
//        }
//
//        log.info("OverdueTaskJob completed. Sent {} notifications.", count);
//    }
//}
