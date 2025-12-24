package com.uniwork.service;

import com.uniwork.model.dto.NotificationDTO;
import com.uniwork.model.entity.Notification;
import com.uniwork.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByNotiIdAndRecipientId(notificationId, userId);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public Long getUnreadCount(Long userId) {
        Long count = (long) notificationRepository.findByRecipientIdAndIsReadFalse(userId).size();
        return count;
    }

    public List<Notification> getAllNotificationByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdAndIsReadFalse(userId);
        return notifications;
    }

    public void sendNotification(Long recipientId, NotificationDTO dto) {

        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setEntityType(dto.getEntityType());
        notification.setEntityId(dto.getEntityId());
        notification.setType(dto.getType());
        notification.setRead(false);
        notification.setCreatedDate(LocalDateTime.now());

        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/notifications/" + recipientId, dto);
    }

}
