package com.uniwork.modules.notification.repository;

import com.uniwork.modules.notification.entity.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification findByNotiIdAndRecipientId(Long notiId, Long userId);

    List<Notification> findByRecipientIdOrderByCreatedDateDesc(Long recipientId);

    List<Notification> findByRecipientIdAndIsReadFalse(Long recipientId);

    Long countByRecipientIdAndIsReadFalse(Long recipientId);

    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipientId = :userId AND n.isRead = false ")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}
