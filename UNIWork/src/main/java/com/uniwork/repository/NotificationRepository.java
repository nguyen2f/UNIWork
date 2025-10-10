package com.uniwork.repository;

import com.uniwork.entity.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification findByNotiId(Long notiId);

    List<Notification> findByRecipientIdOrderByCreatedDateDesc(Long recipientId);
    List<Notification> findByRecipientIdAndIsReadFalse(Long recipientId);
}
