package com.uniwork.repository;

import com.uniwork.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification findByNotiIdAndRecipientId(Long notiId, Long userId);

    List<Notification> findByRecipientIdOrderByCreatedDateDesc(Long recipientId);
    List<Notification> findByRecipientIdAndIsReadFalse(Long recipientId);
}
