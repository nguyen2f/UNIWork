package com.uniwork.service;

import com.uniwork.entity.dto.NotificationDTO;
import com.uniwork.entity.model.Notification;
import com.uniwork.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NoticaitonService {

    private final NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public NoticaitonService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findByNotiId(notificationId);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public ResponseEntity getUnreadCount(Long userId) {
        Long count = (long) notificationRepository.findByRecipientIdAndIsReadFalse(userId).size();
        return ResponseEntity.ok(count);
    }

    public void sendNotification(Long userId, NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setCreatedDate(new Date());
        notification.setMessage(notificationDTO.getMessage());
        notification.setSenderId(userId);
        notification.setRecipientId(notificationDTO.getRecipientId());
        notification.setEntityType(notificationDTO.getEntityType());
        notification.setEntityId(notificationDTO.getEntityId());
        notificationRepository.save(notification);


        messagingTemplate.convertAndSend(
                "/topic/notifications/" + notification.getRecipientId(),
                notification
        );

    }
}
