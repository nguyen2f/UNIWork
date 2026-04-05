package com.uniwork.model.entity;


import com.uniwork.model.enumuration.NotificationEntityType;
import com.uniwork.model.enumuration.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLRestriction("is_deleted = false")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notiId;

    private Long recipientId;

    @Enumerated(EnumType.STRING)
    private NotificationEntityType entityType;

    private Long entityId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;
    private String message;

    @Column(name = "is_read")
    private boolean isRead = false;

    private LocalDateTime createdDate;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean isDeleted = false;
}
