package com.uniwork.model.entity;

import com.uniwork.model.enumuration.ChatRoomType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatRoomType type;

    private String name;

    private LocalDateTime createdAt;
}
