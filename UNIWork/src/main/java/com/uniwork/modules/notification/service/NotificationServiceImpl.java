package com.uniwork.modules.notification.service;

import com.uniwork.modules.notification.dto.NotificationDTO;
import com.uniwork.modules.notification.entity.Notification;
import com.uniwork.modules.notification.repository.NotificationRepository;
import com.uniwork.modules.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByNotiIdAndRecipientId(notificationId, userId);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAsReadAll(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    public Long getUnreadCount(Long userId) {
        Long count = (long) notificationRepository.findByRecipientIdAndIsReadFalse(userId).size();
        return count;
    }

    public List<Notification> getAllNotificationByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(userId);
        return notifications;
    }

    @Async
    public void sendNotification(Long recipientId, NotificationDTO dto) {

        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setEntityType(dto.getEntityType());
        notification.setEntityId(dto.getEntityId());
        notification.setType(dto.getType());
        notification.setRead(false);
        notification.setCreatedDate(LocalDateTime.now());
        String message;
        switch (dto.getType()) {
            case GROUP_ADDED:
                message = "Bạn đã được thêm vào một nhóm mới.";
                break;
            case MESSAGE:
                message = "Bạn có một tin nhắn mới.";
                break;
            case TASK_ASSIGNED:
                message = "Bạn vừa được giao một nhiệm vụ.";
                break;
            default:
                message = "Bạn có một thông báo mới.";
        }
        notification.setMessage(message);

        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/notifications/" + recipientId, dto);
    }

}
