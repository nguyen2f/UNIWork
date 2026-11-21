package com.uniwork.modules.chat.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chat_room_members", indexes = {
        @Index(name = "idx_crm_room", columnList = "roomId"),
        @Index(name = "idx_crm_user", columnList = "userId")
})
@SQLRestriction("is_deleted = false")
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;

    private Long userId;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean isDeleted = false;
}
