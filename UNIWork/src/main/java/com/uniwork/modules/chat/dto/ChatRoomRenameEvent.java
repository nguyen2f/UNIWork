package com.uniwork.modules.chat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomRenameEvent {
    private Long roomId;
    private String newName;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}

