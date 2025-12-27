package com.uniwork.model.dto;

import com.uniwork.model.enumuration.ChatRoomType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomDTO {
    private Long roomId;
    private ChatRoomType type;
    private String name;
    private Long otherUserId;    // 🔥 cho chat 1–1
    private LocalDateTime createdAt;
}

